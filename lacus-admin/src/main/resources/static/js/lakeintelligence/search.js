/**
 * 湖智模块 - 相似检索页交互逻辑
 */
(function() {
    'use strict';

    const LakeCommon = window.LakeCommon;
    let selectedImageFile = null;

    document.addEventListener('DOMContentLoaded', function() {
        initUploadZone();
        initTopKSlider();
    });

    // 初始化上传区
    function initUploadZone() {
        const fileInput = document.getElementById('imageInput');
        const uploadZone = document.getElementById('uploadZone');

        fileInput.addEventListener('change', function(e) {
            if (e.target.files.length > 0) {
                handleImage(e.target.files[0]);
            }
        });

        // 拖拽
        uploadZone.addEventListener('dragover', function(e) {
            e.preventDefault();
            this.classList.add('dragover');
        });
        uploadZone.addEventListener('dragleave', function() {
            this.classList.remove('dragover');
        });
        uploadZone.addEventListener('drop', function(e) {
            e.preventDefault();
            this.classList.remove('dragover');
            if (e.dataTransfer.files.length > 0) {
                handleImage(e.dataTransfer.files[0]);
            }
        });
    }

    // Top-K 滑块
    function initTopKSlider() {
        const topK = document.getElementById('topK');
        const topKValue = document.getElementById('topKValue');
        if (topK) {
            topK.addEventListener('input', function() {
                topKValue.textContent = this.value;
            });
        }
    }

    // 处理图片
    function handleImage(file) {
        const allowedTypes = ['image/jpeg', 'image/png', 'image/bmp'];
        if (!allowedTypes.includes(file.type)) {
            LakeCommon.showToast('仅支持 JPG/PNG/BMP 格式图片', 'warning');
            return;
        }
        const maxSize = 10 * 1024 * 1024; // 10MB
        if (file.size > maxSize) {
            LakeCommon.showToast('图片大小超过 10MB 限制', 'warning');
            return;
        }
        selectedImageFile = file;

        // 预览
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('previewImage').src = e.target.result;
            document.getElementById('previewContainer').classList.remove('d-none');
        };
        reader.readAsDataURL(file);

        // 启用检索按钮
        document.getElementById('searchBtn').disabled = false;
    }

    // 执行检索
    window.performSearch = async function() {
        if (!selectedImageFile) {
            LakeCommon.showToast('请先上传查询图片', 'warning');
            return;
        }

        const topK = parseInt(document.getElementById('topK').value) || 5;
        const vectorIndexId = document.getElementById('vectorSelect').value;

        const formData = new FormData();
        formData.append('image', selectedImageFile);
        formData.append('params', new Blob([JSON.stringify({
            topK: topK,
            vectorIndexId: vectorIndexId ? parseInt(vectorIndexId) : null,
            collectionName: 'image_collection'
        })], { type: 'application/json' }));

        // 显示加载状态
        document.getElementById('emptyState').classList.add('d-none');
        document.getElementById('searchLoading').classList.remove('d-none');
        document.getElementById('resultGrid').classList.add('d-none');

        const searchBtn = document.getElementById('searchBtn');
        LakeCommon.setLoading(searchBtn, true, '检索中...');

        try {
            const result = await fetch('/api/lake-intelligence/search', {
                method: 'POST',
                body: formData
            }).then(function(resp) {
                if (!resp.ok) throw new Error('HTTP ' + resp.status);
                return resp.json();
            }).then(function(res) {
                if (res.code !== undefined && res.code !== 0 && res.code !== 200) {
                    throw new Error(res.msg || '检索失败');
                }
                return res.data !== undefined ? res.data : res;
            });

            renderResults(result);
        } catch (err) {
            LakeCommon.showToast('检索失败：' + err.message, 'error');
            document.getElementById('searchLoading').classList.add('d-none');
            document.getElementById('emptyState').classList.remove('d-none');
        } finally {
            LakeCommon.setLoading(searchBtn, false);
        }
    };

    // 渲染结果
    function renderResults(data) {
        document.getElementById('searchLoading').classList.add('d-none');
        const grid = document.getElementById('resultGrid');
        grid.innerHTML = '';

        const results = data.results || [];
        document.getElementById('resultCount').textContent = '共 ' + results.length + ' 个结果';

        if (results.length === 0) {
            grid.innerHTML = '<div class="col-12 text-center text-muted py-4">未找到相似图片</div>';
            grid.classList.remove('d-none');
            return;
        }

        results.forEach(function(item, idx) {
            const similarity = item.similarity !== undefined ? item.similarity :
                              (item.distance !== undefined ? (1 - item.distance) : 0);
            const pct = Math.round(similarity * 100);

            let badgeClass = 'similarity-low';
            if (pct >= 70) badgeClass = 'similarity-high';
            else if (pct >= 40) badgeClass = 'similarity-medium';

            const col = document.createElement('div');
            col.className = 'col-6 col-md-4 col-lg-3';
            col.innerHTML = `
                <div class="card lake-card h-100">
                    <div class="position-relative">
                        <img src="/api/lake-intelligence/image/${item.id}"
                             class="card-img-top" alt="结果 ${idx + 1}"
                             style="height: 140px; object-fit: cover;"
                             loading="lazy"
                             onerror="this.src='/images/placeholder.png'">
                        <span class="similarity-badge ${badgeClass} position-absolute top-0 end-0 m-2">
                            ${pct}%
                        </span>
                    </div>
                    <div class="card-body p-2">
                        <small class="text-muted">#${idx + 1} · ID: ${item.id}</small>
                    </div>
                </div>`;
            grid.appendChild(col);
        });

        grid.classList.remove('d-none');
    }
})();
