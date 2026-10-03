/**
 * 湖智模块 - 向量构建页交互逻辑
 */
(function() {
    'use strict';

    const LakeCommon = window.LakeCommon;
    let buildTimer = null;
    let currentBuildId = null;

    document.addEventListener('DOMContentLoaded', function() {
        loadDatasets();
        loadModels();
    });

    // 加载数据集列表
    async function loadDatasets() {
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
            sel.innerHTML = '<option value="">加载失败</option>';
        }
    }

    // 加载模型列表
    async function loadModels() {
        const sel = document.getElementById('modelSelect');
        try {
            const data = await LakeCommon.request('/api/lake-intelligence/models?page=1&pageSize=100');
            const list = data.rows || data.list || data || [];
            sel.innerHTML = '<option value="">请选择训练完成的模型...</option>';
            list.forEach(function(m) {
                const opt = document.createElement('option');
                opt.value = m.modelId;
                opt.textContent = m.modelName + ' (轮数: ' + (m.trainingEpochs || '-') + ')';
                sel.appendChild(opt);
            });
        } catch (err) {
            sel.innerHTML = '<option value="">加载失败</option>';
        }
    }

    // 开始构建
    window.startBuild = async function() {
        const datasetId = document.getElementById('datasetSelect').value;
        const modelId = document.getElementById('modelSelect').value;
        const indexName = document.getElementById('indexName').value;
        const collectionName = document.getElementById('collectionName').value;
        const distanceMetric = document.getElementById('distanceMetric').value;
        const batchSize = parseInt(document.getElementById('batchSize').value) || 32;

        if (!datasetId) { LakeCommon.showToast('请选择图片库', 'warning'); return; }
        if (!modelId) { LakeCommon.showToast('请选择模型', 'warning'); return; }
        if (!indexName) { LakeCommon.showToast('请输入向量库名称', 'warning'); return; }

        const buildBtn = document.getElementById('buildBtn');
        LakeCommon.setLoading(buildBtn, true, '提交中...');

        try {
            const result = await LakeCommon.request('/api/lake-intelligence/vectors/build', {
                method: 'POST',
                body: JSON.stringify({
                    indexName: indexName,
                    datasetId: parseInt(datasetId),
                    modelId: parseInt(modelId),
                    collectionName: collectionName,
                    batchSize: batchSize,
                    distanceMetric: distanceMetric,
                    creatorId: 'current-user'
                })
            });

            currentBuildId = result.indexId || result.taskId || result;
            LakeCommon.showToast('向量构建任务已启动！', 'success');

            // 切换 UI
            document.getElementById('configCard').classList.add('d-none');
            document.getElementById('progressCard').classList.remove('d-none');

            // 开始轮询
            pollBuildProgress();
        } catch (err) {
            LakeCommon.showToast('启动失败：' + err.message, 'error');
        } finally {
            LakeCommon.setLoading(buildBtn, false);
        }
    };

    // 轮询构建进度
    async function pollBuildProgress() {
        if (!currentBuildId) return;
        try {
            const data = await LakeCommon.request('/api/lake-intelligence/vectors/' + currentBuildId + '/progress');

            const indexed = data.progress || data.indexedCount || 0;
            const total = data.total || data.totalCount || 1;
            const pct = Math.round((indexed / total) * 100);

            document.getElementById('buildStatus').textContent =
                data.status === 'building' ? '构建中...' : (data.status || '处理中');
            document.getElementById('progressText').textContent = indexed + ' / ' + total;

            const bar = document.getElementById('progressBar');
            bar.style.width = pct + '%';
            bar.textContent = pct + '%';

            if (data.message) {
                document.getElementById('buildMessage').textContent = data.message;
            }

            if (data.status === 'building' || data.status === 'BUILDING') {
                buildTimer = setTimeout(pollBuildProgress, 2000);
            } else if (data.status === 'completed' || data.status === 'COMPLETED') {
                document.getElementById('buildStatus').textContent = '构建完成';
                document.getElementById('progressBar').classList.remove('progress-bar-animated');
                document.getElementById('completeMessage').textContent = '共索引 ' + total + ' 张图片';
                document.getElementById('completedActions').classList.remove('d-none');
            } else if (data.status === 'failed' || data.status === 'FAILED') {
                document.getElementById('buildStatus').textContent = '构建失败';
                document.getElementById('buildMessage').textContent = data.error || '构建过程发生错误';
            }
        } catch (err) {
            document.getElementById('buildMessage').textContent = '获取进度失败：' + err.message;
            buildTimer = setTimeout(pollBuildProgress, 5000);
        }
    }
})();
