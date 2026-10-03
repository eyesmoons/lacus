"""SimilarityAutoEncoder 模型测试"""
import pytest
import torch

from models.similarity_autoencoder import SimilarityAutoEncoder


class TestSimilarityAutoEncoder:
    """测试 SimilarityAutoEncoder 模型"""

    def test_encode_output_shape(self):
        """测试 encode() 输出 shape 为 (N, 512)"""
        model = SimilarityAutoEncoder()
        model.eval()
        # 创建模拟输入批次 (N, 3, 64, 64)
        batch_size = 8
        input_tensor = torch.randn(batch_size, 3, 64, 64)

        with torch.no_grad():
            embeddings = model.encode(input_tensor)

        # 验证输出 shape
        assert embeddings.shape == (batch_size, 512), \
            f"期望 shape ({batch_size}, 512)，实际得到 {embeddings.shape}"

    def test_decode_output_shape(self):
        """测试 decode() 输出 shape 为 (N, 3, 64, 64)"""
        model = SimilarityAutoEncoder()
        model.eval()
        batch_size = 8
        input_tensor = torch.randn(batch_size, 3, 64, 64)

        with torch.no_grad():
            embeddings = model.encode(input_tensor)
            reconstructed = model.decode(embeddings)

        assert reconstructed.shape == (batch_size, 3, 64, 64), \
            f"期望 shape ({batch_size}, 3, 64, 64)，实际得到 {reconstructed.shape}"

    def test_forward_roundtrip(self):
        """测试前向传播往返一致性"""
        model = SimilarityAutoEncoder()
        model.eval()
        input_tensor = torch.randn(4, 3, 64, 64)

        with torch.no_grad():
            output = model(input_tensor)

        # 输出应与输入同 shape
        assert output.shape == input_tensor.shape, \
            f"期望 shape {input_tensor.shape}，实际得到 {output.shape}"

    def test_encoder_has_six_conv_layers(self):
        """验证编码器包含 6 层 Conv2d"""
        model = SimilarityAutoEncoder()
        conv_layers = [m for m in model.encoder.modules() if isinstance(m, torch.nn.Conv2d)]
        assert len(conv_layers) == 6, f"编码器应有 6 层 Conv2d，实际 {len(conv_layers)} 层"

    def test_decoder_has_six_deconv_layers(self):
        """验证解码器包含 6 层 ConvTranspose2d"""
        model = SimilarityAutoEncoder()
        deconv_layers = [m for m in model.decoder.modules() if isinstance(m, torch.nn.ConvTranspose2d)]
        assert len(deconv_layers) == 6, f"解码器应有 6 层 ConvTranspose2d，实际 {len(deconv_layers)} 层"

    def test_single_image_encoding(self):
        """测试单张图片编码"""
        model = SimilarityAutoEncoder()
        model.eval()
        input_tensor = torch.randn(1, 3, 64, 64)

        with torch.no_grad():
            embeddings = model.encode(input_tensor)

        assert embeddings.shape == (1, 512)

    def test_training_mode_has_gradients(self):
        """测试训练模式下梯度可传播"""
        model = SimilarityAutoEncoder()
        model.train()
        input_tensor = torch.randn(2, 3, 64, 64)

        output = model(input_tensor)
        loss = output.mean()
        loss.backward()

        # 验证至少部分参数有梯度
        has_grad = any(p.grad is not None for p in model.parameters())
        assert has_grad, "训练模式下参数应能接收梯度"
