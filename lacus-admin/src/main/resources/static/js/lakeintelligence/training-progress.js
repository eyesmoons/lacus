/**
 * 湖智模块 - 训练进度页交互逻辑
 */
(function() {
    'use strict';

    const LakeCommon = window.LakeCommon;
    let pollTimer = null;
    let lossChart = null;
    let currentTaskId = null;

    document.addEventListener('DOMContentLoaded', function() {
        const pathParts = window.location.pathname.split('/');
        currentTaskId = pathParts[pathParts.length - 1];
        if (currentTaskId) {
            initChart();
            pollProgress();
        }
    });

    // 初始化 Chart.js
    function initChart() {
        const ctx = document.getElementById('lossChart');
        if (!ctx) return;
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

    // 轮询进度
    async function pollProgress() {
        try {
            const data = await LakeCommon.request('/api/lake-intelligence/tasks/' + currentTaskId + '/progress');
            renderProgress(data);

            if (data.status === 'training' || data.status === 'PENDING') {
                pollTimer = setTimeout(pollProgress, 2000);
            } else {
                // 终态，停止轮询
                document.getElementById('cancelBtn').classList.add('d-none');
                if (data.status === 'completed' || data.status === 'COMPLETED') {
                    document.getElementById('completedActions').classList.remove('d-none');
                }
            }
        } catch (err) {
            document.getElementById('progressMessage').textContent = '获取进度失败：' + err.message;
            pollTimer = setTimeout(pollProgress, 5000);
        }
    }

    // 渲染进度
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
        document.getElementById('taskStatus').innerHTML = statusMap[data.status] || data.status;

        const current = data.progress || data.currentEpoch || 0;
        const total = data.total || data.totalEpochs || 1;
        document.getElementById('currentEpoch').textContent = current + ' / ' + total;

        const pct = Math.round((current / total) * 100);
        const bar = document.getElementById('progressBar');
        bar.style.width = pct + '%';
        bar.textContent = pct + '%';
        document.getElementById('progressText').textContent = pct + '%';

        if (data.trainLoss !== null && data.trainLoss !== undefined) {
            document.getElementById('trainLoss').textContent = data.trainLoss.toFixed(4);
        }
        if (data.valLoss !== null && data.valLoss !== undefined) {
            document.getElementById('valLoss').textContent = data.valLoss.toFixed(4);
        }
        if (data.message) {
            document.getElementById('progressMessage').textContent = data.message;
        }

        // 更新图表
        if (lossChart && data.lossHistory) {
            let history = data.lossHistory;
            if (typeof history === 'string') {
                try { history = JSON.parse(history); } catch (e) { history = []; }
            }
            if (Array.isArray(history) && history.length > 0) {
                document.getElementById('noChartData').classList.add('d-none');
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
