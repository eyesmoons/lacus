/**
 * 湖智模块 - 训练相关交互逻辑（配置页 + 进度页）
 */
(function() {
    'use strict';

    const LakeCommon = window.LakeCommon;

    // ===================== 训练配置页 =====================
    const trainForm = document.getElementById('trainForm');
    if (trainForm) {
        initTrainingConfigPage();
    }

    // ===================== 训练进度页 =====================
    const progressBar = document.getElementById('progressBar');
    if (progressBar) {
        initTrainingProgressPage();
    }

    // ===================== 配置页逻辑 =====================
    function initTrainingConfigPage() {
        initConfigSliders();
        loadConfigDatasets();

        // URL 参数预选
        const urlParams = new URLSearchParams(window.location.search);
        const preselectId = urlParams.get('datasetId');
        if (preselectId) {
            const checkExist = setInterval(function() {
                const sel = document.getElementById('datasetSelect');
                if (sel && sel.options.length > 1) {
                    sel.value = preselectId;
                    clearInterval(checkExist);
                }
            }, 200);
        }

        trainForm.addEventListener('submit', function(e) {
            e.preventDefault();
            if (this.checkValidity()) {
                startTraining();
            } else {
                this.classList.add('was-validated');
            }
        });
    }

    function initConfigSliders() {
        const epochsRange = document.getElementById('epochsRange');
        const epochsValue = document.getElementById('epochsValue');
        const previewEpochs = document.getElementById('previewEpochs');
        if (epochsRange) {
            epochsRange.addEventListener('input', function() {
                epochsValue.textContent = this.value;
                previewEpochs.textContent = this.value;
            });
        }

        const lrRange = document.getElementById('lrRange');
        const lrValue = document.getElementById('lrValue');
        const previewLr = document.getElementById('previewLr');
        if (lrRange) {
            lrRange.addEventListener('input', function() {
                const val = Math.pow(10, parseFloat(this.value));
                const display = val.toFixed(4).replace(/\.?0+$/, '');
                lrValue.textContent = display;
                previewLr.textContent = display;
            });
        }

        document.querySelectorAll('input[name="batchSize"]').forEach(function(radio) {
            radio.addEventListener('change', function() {
                document.getElementById('previewBatch').textContent = this.value;
            });
        });

        const deviceSelect = document.getElementById('deviceSelect');
        if (deviceSelect) {
            deviceSelect.addEventListener('change', function() {
                document.getElementById('previewDevice').textContent =
                    this.value === 'cuda' ? 'CUDA (GPU)' : 'CPU';
            });
        }
    }

    async function loadConfigDatasets() {
        const sel = document.getElementById('datasetSelect');
        try {
            const data = await LakeCommon.request('/api/lake-intelligence/datasets?page=1&pageSize=100');
            const list = data.rows || data.list || data || [];
            sel.innerHTML = '<option value="">请选择数据集...</option>';
            list.forEach(function(ds) {
                const opt = document.createElement('option');
                opt.value = ds.datasetId;
                opt.textContent = ds.datasetName + ' (' + (ds.imageCount || 0) + ' 张)';
                sel.appendChild(opt);
            });
        } catch (err) {
            sel.innerHTML = '<option value="">加载失败，请刷新重试</option>';
        }
    }

    async function startTraining() {
        const startBtn = document.getElementById('startBtn');
        const payload = {
            taskName: document.getElementById('taskName').value,
            taskType: document.getElementById('taskType').value,
            datasetId: parseInt(document.getElementById('datasetSelect').value),
            trainerType: document.getElementById('trainerType').value,
            epochs: parseInt(document.getElementById('epochsRange').value),
            learningRate: Math.pow(10, parseFloat(document.getElementById('lrRange').value)),
            batchSize: parseInt(document.querySelector('input[name="batchSize"]:checked').value),
            device: document.getElementById('deviceSelect').value,
            creatorId: 'current-user'
        };

        LakeCommon.setLoading(startBtn, true, '启动中...');

        try {
            const result = await LakeCommon.request('/api/lake-intelligence/tasks', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
            LakeCommon.showToast('训练任务已启动！', 'success');
            setTimeout(function() {
                window.location.href = '/lake-intelligence/training/' + (result.taskId || result);
            }, 1000);
        } catch (err) {
            LakeCommon.showToast('启动失败：' + err.message, 'error');
        } finally {
            LakeCommon.setLoading(startBtn, false);
        }
    }

    // ===================== 进度页逻辑 =====================
    let pollTimer = null;
    let lossChart = null;
    let currentTaskId = null;

    function initTrainingProgressPage() {
        const pathParts = window.location.pathname.split('/');
        currentTaskId = pathParts[pathParts.length - 1];
        if (currentTaskId) {
            initLossChart();
            pollTrainingProgress();
        }
    }

    function initLossChart() {
        const ctx = document.getElementById('lossChart');
        if (typeof Chart === 'undefined' || !ctx) return;
        lossChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [
                    {
                        label: '训练损失',
                        data: [],
                        borderColor: '#0d6efd',
                        backgroundColor: 'rgba(13, 110, 253, 0.1)',
                        borderWidth: 2,
                        tension: 0.3,
                        fill: true,
                        pointRadius: 2
                    },
                    {
                        label: '验证损失',
                        data: [],
                        borderColor: '#ffc107',
                        backgroundColor: 'rgba(255, 193, 7, 0.1)',
                        borderWidth: 2,
                        tension: 0.3,
                        fill: true,
                        pointRadius: 2
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'top' }
                },
                scales: {
                    x: { title: { display: true, text: '轮次' } },
                    y: { title: { display: true, text: '损失值' }, beginAtZero: true }
                }
            }
        });
    }

    async function pollTrainingProgress() {
        try {
            const data = await LakeCommon.request('/api/lake-intelligence/tasks/' + currentTaskId + '/progress');
            renderProgress(data);

            if (data.status === 'training' || data.status === 'PENDING') {
                pollTimer = setTimeout(pollTrainingProgress, 2000);
            } else {
                document.getElementById('cancelBtn').classList.add('d-none');
                if (data.status === 'completed' || data.status === 'COMPLETED') {
                    document.getElementById('completedActions').classList.remove('d-none');
                }
            }
        } catch (err) {
            const msgEl = document.getElementById('progressMessage');
            if (msgEl) msgEl.textContent = '获取进度失败：' + err.message;
            pollTimer = setTimeout(pollTrainingProgress, 5000);
        }
    }

    function renderProgress(data) {
        const statusMap = {
            'training': '<span class="badge bg-primary">训练中</span>',
            'PENDING': '<span class="badge bg-secondary">等待中</span>',
            'completed': '<span class="badge bg-success">已完成</span>',
            'COMPLETED': '<span class="badge bg-success">已完成</span>',
            'failed': '<span class="badge bg-danger">失败</span>',
            'FAILED': '<span class="badge bg-danger">失败</span>',
            'cancelled': '<span class="badge bg-warning">已取消</span>',
            'CANCELLED': '<span class="badge bg-warning">已取消</span>'
        };

        const statusEl = document.getElementById('taskStatus');
        if (statusEl) statusEl.innerHTML = statusMap[data.status] || data.status;

        const current = data.progress || data.currentEpoch || 0;
        const total = data.total || data.totalEpochs || 1;

        const epochEl = document.getElementById('currentEpoch');
        if (epochEl) epochEl.textContent = current + ' / ' + total;

        const pct = Math.round((current / total) * 100);
        const bar = document.getElementById('progressBar');
        if (bar) {
            bar.style.width = pct + '%';
            bar.textContent = pct + '%';
        }
        const pctEl = document.getElementById('progressText');
        if (pctEl) pctEl.textContent = pct + '%';

        const trainLossEl = document.getElementById('trainLoss');
        if (trainLossEl && data.trainLoss !== null && data.trainLoss !== undefined) {
            trainLossEl.textContent = data.trainLoss.toFixed(4);
        }
        const valLossEl = document.getElementById('valLoss');
        if (valLossEl && data.valLoss !== null && data.valLoss !== undefined) {
            valLossEl.textContent = data.valLoss.toFixed(4);
        }
        const msgEl = document.getElementById('progressMessage');
        if (msgEl && data.message) msgEl.textContent = data.message;

        // 更新图表
        if (lossChart && data.lossHistory) {
            let history = data.lossHistory;
            if (typeof history === 'string') {
                try { history = JSON.parse(history); } catch (e) { history = []; }
            }
            if (Array.isArray(history) && history.length > 0) {
                const noData = document.getElementById('noChartData');
                if (noData) noData.classList.add('d-none');
                lossChart.data.labels = history.map(function(_, i) { return i + 1; });
                lossChart.data.datasets[0].data = history.map(function(item) { return item.trainLoss; });
                lossChart.data.datasets[1].data = history.map(function(item) { return item.valLoss; });
                lossChart.update();
            }
        }
    }

    // 取消训练
    window.cancelTraining = async function() {
        if (!currentTaskId) return;
        if (!confirm('确认取消当前训练任务？')) return;

        try {
            await LakeCommon.request('/api/lake-intelligence/tasks/' + currentTaskId + '/cancel', {
                method: 'POST'
            });
            LakeCommon.showToast('训练任务已取消', 'info');
            if (pollTimer) clearTimeout(pollTimer);
            document.getElementById('cancelBtn').classList.add('d-none');
        } catch (err) {
            LakeCommon.showToast('取消失败：' + err.message, 'error');
        }
    };
})();
