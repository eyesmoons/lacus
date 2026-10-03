/**
 * 湖智模块 - 数据集预览页交互逻辑
 */
(function() {
    'use strict';

    const LakeCommon = window.LakeCommon;
    let currentDatasetId = null;

    document.addEventListener('DOMContentLoaded', function() {
        // 从 URL 提取 datasetId
        const pathParts = window.location.pathname.split('/');
        const idx = pathParts.indexOf('dataset');
        if (idx >= 0 && pathParts[idx + 1]) {
            currentDatasetId = pathParts[idx + 1];
        }
        if (currentDatasetId) {
            loadDataset(currentDatasetId);
        } else {
            showError('缺少数据集 ID');
        }

        // 设置开始训练按钮链接
        const startTrainBtn = document.getElementById('startTrainBtn');
        if (startTrainBtn && currentDatasetId) {
            startTrainBtn.href = '/lake-intelligence/training/new?datasetId=' + currentDatasetId;
        }
    });

    // 加载数据集详情
    async function loadDataset(id) {
        try {
            const data = await LakeCommon.request('/api/lake-intelligence/datasets/' + id + '/preview');
            renderDataset(data);
        } catch (err) {
            showError(err.message || '加载数据集失败');
        }
    }

    // 渲染数据集信息
    function renderDataset(data) {
        document.getElementById('loadingSpinner').classList.add('d-none');
        document.getElementById('datasetContent').classList.remove('d-none');

        document.getElementById('metaName').textContent = data.datasetName || '-';
        document.getElementById('metaDesc').textContent = data.description || '无描述';
        document.getElementById('metaSource').textContent = data.storageSource || '-';

        const statusEl = document.getElementById('metaStatus');
        const statusMap = {
            'READY': '<span class="badge bg-success">就绪</span>',
            'PROCESSING': '<span class="badge bg-info">处理中</span>',
            'WAITING_DOWNLOAD': '<span class="badge bg-warning">等待下载</span>',
            'DOWNLOADING': '<span class="badge bg-primary">下载中</span>',
            'ERROR': '<span class="badge bg-danger">错误</span>'
        };
        statusEl.innerHTML = statusMap[data.status] || '<span class="badge bg-secondary">' + (data.status || '-') + '</span>';

        document.getElementById('metaImageCount').textContent = data.imageCount || 0;
        document.getElementById('metaTotalSize').textContent = LakeCommon.formatFileSize(data.totalSizeBytes);
        document.getElementById('metaCreateTime').textContent = LakeCommon.formatDate(data.createTime);

        // 缩略图
        const grid = document.getElementById('thumbnailGrid');
        const empty = document.getElementById('emptyThumbnails');
        grid.innerHTML = '';
        if (data.imageList && data.imageList.length > 0) {
            document.getElementById('thumbnailCount').textContent = '共 ' + data.imageList.length + ' 张';
            empty.classList.add('d-none');
            data.imageList.forEach(function(img) {
                const item = document.createElement('div');
                item.className = 'thumbnail-item';
                item.innerHTML = '<img src="' + (img.thumbnailUrl || img.url || '/images/placeholder.png') + '" alt="' + (img.name || '') + '" loading="lazy">';
                grid.appendChild(item);
            });
        } else {
            document.getElementById('thumbnailCount').textContent = '共 0 张';
            empty.classList.remove('d-none');
        }
    }

    // 显示错误
    function showError(msg) {
        document.getElementById('loadingSpinner').classList.add('d-none');
        document.getElementById('errorState').classList.remove('d-none');
        document.getElementById('errorMessage').textContent = msg;
    }

    // 删除数据集
    window.deleteDataset = async function() {
        if (!currentDatasetId) return;
        if (!confirm('确认删除此数据集？此操作不可恢复。')) return;

        try {
            await LakeCommon.request('/api/lake-intelligence/datasets/' + currentDatasetId, {
                method: 'DELETE'
            });
            LakeCommon.showToast('数据集已删除', 'success');
            setTimeout(function() {
                window.location.href = '/lake-intelligence/dataset/upload';
            }, 1000);
        } catch (err) {
            LakeCommon.showToast('删除失败：' + err.message, 'error');
        }
    };
})();
