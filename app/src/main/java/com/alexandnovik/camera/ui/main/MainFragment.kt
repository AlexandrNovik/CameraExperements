package com.alexandnovik.camera.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alexandnovik.camera.data.camera.AppCamera
import com.alexandnovik.camera.databinding.FragmentMainBinding
import com.alexandnovik.camera.utils.extensions.setTopInsetsMargin
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class MainFragment : Fragment() {
    companion object {
        private const val CAMERA_PERMISSION = 10
    }

    private val viewModel: MainViewModel by viewModel()
    private val camera: AppCamera = getKoin().get(named("camera"))
    private val gLSurfaceView: GLSurfaceView = getKoin().get(named("gLSurfaceView"))

    private var binding: FragmentMainBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding!!.root.apply { setTopInsetsMargin() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addGlSurfaceView()
        requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION)
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.closeCamera()
        removeGlSurfaceView()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != CAMERA_PERMISSION) {
            throw IllegalStateException("Unknown permission request = $requestCode")
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            tryOpenCamera()
        } else {
            // TODO: show ui for no permission
        }
    }

    private fun addGlSurfaceView() {
        gLSurfaceView.parent?.let { (it as ViewGroup).removeView(gLSurfaceView) }
        binding?.let {
            with(it) {
                mainCameraContainer.addView(gLSurfaceView, 0,
                    ViewGroup.LayoutParams(mainCameraContainer.layoutParams)
                        .apply {
                            width = ViewGroup.LayoutParams.MATCH_PARENT
                            height = ViewGroup.LayoutParams.MATCH_PARENT
                        })
            }
        }
    }

    private fun removeGlSurfaceView() {
        gLSurfaceView.parent?.let { (it as ViewGroup).removeView(gLSurfaceView) }
        binding?.let {
            with(it) {
                mainCameraContainer.removeAllViews()
            }
        }
    }

    private fun tryOpenCamera() {
        binding?.let {
            Handler(Looper.getMainLooper()).post {
                camera.openCamera(it.mainCameraContainer, this)
            }
        }
    }
}
