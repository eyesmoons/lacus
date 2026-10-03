/**
 * 湖智模块 - 训练配置页交互逻辑
 */
(function() {
    'use strict';

    const LakeCommon = window.LakeCommon;

    document.addEventListener('DOMContentLoaded', function() {
        initSliders();
        loadDatasets();

        // 从 URL 参数预选数据集
        const urlParams = new URLSearchParams(window.location.search);
        const preselectId = urlParams.get('datasetId');
        if (preselectId) {
            const checkExist = setInterval(function() {
                const sel = document.getElementById('datasetSelect');
                if (sel.options.length > 1) {
                    sel.value = preselectId;
                    clearInterval(checkExist);
                }
            }, 200);
        }

        // 表单提交
        document.getElementById('trainForm').addEventListener('submit', function(e) {
            e.preventDefault();
            if (this.checkValidity()) {
                startTraining();
            } else {
                this.classList.add('was-validated');
            }
        });
    });

    // 滑块交互
    function initSliders() {
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

        // 批次大小预览
        document.querySelectorAll('input[name="batchSize"]').forEach(function(radio) {
            radio.addEventListener('change', function() {
                document.getElementById('previewBatch').textContent = this.value;
            });
        });

        // 设备预览
        const deviceSelect = document.getElementById('deviceSelect');
        if (deviceSelect) {
            deviceSelect.addEventListener('change', function() {
                document.getElementById('previewDevice').textContent =
                    this.value === 'cuda' ? 'CUDA (GPU)' : 'CPU';
            });
        }
    }

    // 加载数据集列表
    async function loadDatasets() {
        const sel = document.getElementById('datasetSelect');
        try {
            const data = await LakeCommon.request('/api/lake-intelligence/datasets?page=1&pageSize=100');
            const list = data.rows || data.list || data || [];
            sel.innerHTML = '<option value="">请选择数据集...</option>';
            list.forEach(function(ds) {
                if (ds.status === 'READY') {
                    const opt = document.createElement('option');
                    opt.value = ds.datasetId;
                    opt.textContent = ds.datasetName + ' (' + (ds.imageCount || 0) + ' 张)';
                    sel.appendChild(opt);
                }
            });
        } catch (err) {
            sel.innerHTML = '<option value="">加载失败，请刷新重试</option>';
        }
    }

    // 启动训练
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
})();
