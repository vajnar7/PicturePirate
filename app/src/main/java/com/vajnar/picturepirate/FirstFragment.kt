package com.vajnar.picturepirate

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vajnar.picturepirate.databinding.FragmentFirstBinding
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var pendingPictureCallback: ((Bitmap?) -> Unit)? = null

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview(),
    ) { bitmap: Bitmap? ->
        pendingPictureCallback?.invoke(bitmap)
        pendingPictureCallback = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonTakePicture.setOnClickListener {
            takePicture { bitmap ->
                bitmap?.let {
                    binding.imageViewPicture.setImageBitmap(it)
                    Pirate.processImageForText(it, binding)
                }
            }
        }
    }

    /**
     * Launches the camera intent to take a picture and delivers the snapped [Bitmap] via callback.
     *
     * @param onResult Callback invoked with the snapped [Bitmap], or null if cancelled/failed.
     */
    fun takePicture(onResult: (Bitmap?) -> Unit) {
        pendingPictureCallback = onResult
        takePictureLauncher.launch(null)
    }

    /**
     * Coroutine-friendly function that launches the camera intent to take a picture
     * and returns the resulting snapped [Bitmap].
     *
     * @return Snapped [Bitmap], or null if cancelled/failed.
     */
    @Suppress("unused")
    suspend fun takePicture(): Bitmap? = suspendCancellableCoroutine { continuation ->
        takePicture { bitmap ->
            if (continuation.isActive) {
                continuation.resume(bitmap)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}