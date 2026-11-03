package com.example.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "CameraPreview"

class CameraController(
    val context: Context,
    private val onCameraReady: () -> Unit = {}
) {
    var imageCapture: ImageCapture? = null
    var camera: Camera? = null
    var isFrontCamera by mutableStateOf(false)
    var isTorchOn by mutableStateOf(false)
    var hasCameraPermission by mutableStateOf(false)

    suspend fun captureBitmap(): Bitmap = suspendCoroutine { continuation ->
        val capture = imageCapture
        if (capture == null) {
            continuation.resumeWithException(IllegalStateException("Camera capture is not initialized"))
            return@suspendCoroutine
        }

        val executor = ContextCompat.getMainExecutor(context)
        capture.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    try {
                        val rotationDegrees = image.imageInfo.rotationDegrees
                        val buffer: ByteBuffer = image.planes[0].buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)
                        val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                        val finalBitmap = if (rotationDegrees != 0 || isFrontCamera) {
                            val matrix = Matrix()
                            matrix.postRotate(rotationDegrees.toFloat())
                            if (isFrontCamera) {
                                matrix.postScale(-1f, 1f) // Mirror front camera
                            }
                            Bitmap.createBitmap(
                                originalBitmap,
                                0,
                                0,
                                originalBitmap.width,
                                originalBitmap.height,
                                matrix,
                                true
                            )
                        } else {
                            originalBitmap
                        }
                        continuation.resume(finalBitmap)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing captured frame", e)
                        continuation.resumeWithException(e)
                    } finally {
                        image.close()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Camera capture failed", exception)
                    continuation.resumeWithException(exception)
                }
            }
        )
    }

    fun toggleTorch(): Boolean {
        val currentCamera = camera
        if (currentCamera != null && currentCamera.cameraInfo.hasFlashUnit()) {
            isTorchOn = !isTorchOn
            currentCamera.cameraControl.enableTorch(isTorchOn)
        }
        return isTorchOn
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    controller: CameraController,
    showGrid: Boolean = true,
    showAlignmentGuide: Boolean = true
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    LaunchedEffect(controller.isFrontCamera, controller.hasCameraPermission) {
        if (!controller.hasCameraPermission) return@LaunchedEffect

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                controller.imageCapture = imageCapture

                val cameraSelector = if (controller.isFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                controller.camera = camera
            } catch (e: Exception) {
                Log.e(TAG, "Failed to bind camera use cases", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay guides
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Rule of thirds grid
            if (showGrid) {
                val gridColor = Color.White.copy(alpha = 0.22f)
                val strokeWidth = 1.5f
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)

                // Vertical grid lines
                drawLine(
                    color = gridColor,
                    start = Offset(width / 3f, 0f),
                    end = Offset(width / 3f, height),
                    strokeWidth = strokeWidth,
                    pathEffect = dashEffect
                )
                drawLine(
                    color = gridColor,
                    start = Offset(width * 2f / 3f, 0f),
                    end = Offset(width * 2f / 3f, height),
                    strokeWidth = strokeWidth,
                    pathEffect = dashEffect
                )

                // Horizontal grid lines
                drawLine(
                    color = gridColor,
                    start = Offset(0f, height / 3f),
                    end = Offset(width, height / 3f),
                    strokeWidth = strokeWidth,
                    pathEffect = dashEffect
                )
                drawLine(
                    color = gridColor,
                    start = Offset(0f, height * 2f / 3f),
                    end = Offset(width, height * 2f / 3f),
                    strokeWidth = strokeWidth,
                    pathEffect = dashEffect
                )
            }

            // 2. Paper Sheet Framing Reticle
            if (showAlignmentGuide) {
                val reticlePaddingX = width * 0.08f
                val reticlePaddingY = height * 0.10f
                val rectWidth = width - (reticlePaddingX * 2)
                val rectHeight = height - (reticlePaddingY * 2)

                // Outer dimmed vignette border
                drawRoundRect(
                    color = Color(0xFFFFB84D).copy(alpha = 0.45f),
                    topLeft = Offset(reticlePaddingX, reticlePaddingY),
                    size = Size(rectWidth, rectHeight),
                    cornerRadius = CornerRadius(24f, 24f),
                    style = Stroke(
                        width = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 14f), 0f)
                    )
                )

                // Corner bracket accents
                val bracketLen = 36f
                val bracketColor = Color(0xFFFFB84D).copy(alpha = 0.85f)
                val bracketStroke = 6f

                // Top-Left corner
                drawLine(
                    color = bracketColor,
                    start = Offset(reticlePaddingX, reticlePaddingY),
                    end = Offset(reticlePaddingX + bracketLen, reticlePaddingY),
                    strokeWidth = bracketStroke
                )
                drawLine(
                    color = bracketColor,
                    start = Offset(reticlePaddingX, reticlePaddingY),
                    end = Offset(reticlePaddingX, reticlePaddingY + bracketLen),
                    strokeWidth = bracketStroke
                )

                // Top-Right corner
                val trX = reticlePaddingX + rectWidth
                drawLine(
                    color = bracketColor,
                    start = Offset(trX, reticlePaddingY),
                    end = Offset(trX - bracketLen, reticlePaddingY),
                    strokeWidth = bracketStroke
                )
                drawLine(
                    color = bracketColor,
                    start = Offset(trX, reticlePaddingY),
                    end = Offset(trX, reticlePaddingY + bracketLen),
                    strokeWidth = bracketStroke
                )

                // Bottom-Left corner
                val blY = reticlePaddingY + rectHeight
                drawLine(
                    color = bracketColor,
                    start = Offset(reticlePaddingX, blY),
                    end = Offset(reticlePaddingX + bracketLen, blY),
                    strokeWidth = bracketStroke
                )
                drawLine(
                    color = bracketColor,
                    start = Offset(reticlePaddingX, blY),
                    end = Offset(reticlePaddingX, blY - bracketLen),
                    strokeWidth = bracketStroke
                )

                // Bottom-Right corner
                drawLine(
                    color = bracketColor,
                    start = Offset(trX, blY),
                    end = Offset(trX - bracketLen, blY),
                    strokeWidth = bracketStroke
                )
                drawLine(
                    color = bracketColor,
                    start = Offset(trX, blY),
                    end = Offset(trX, blY - bracketLen),
                    strokeWidth = bracketStroke
                )
            }
        }
    }
}
