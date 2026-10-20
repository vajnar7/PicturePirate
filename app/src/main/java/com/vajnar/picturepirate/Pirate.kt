package com.vajnar.picturepirate

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.vajnar.picturepirate.databinding.FragmentFirstBinding

object Pirate {
    fun processImageForText(bitmap: Bitmap, binding: FragmentFirstBinding) {
        // 1. Initialize the Text Recognizer (Latin script)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // 2. Prepare the InputImage
        val image = InputImage.fromBitmap(bitmap, 0)

        // 3. Process the Image
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // The full text extracted from the image
                val resultText = visionText.text
                binding.textviewFirst.text = resultText

                // Loop through text blocks
                for (block in visionText.textBlocks) {
                    val blockText = block.text
                    val blockCornerPoints = block.cornerPoints
                    val blockFrame = block.boundingBox

                    // Loop through lines in each block
                    for (line in block.lines) {
                        val lineText = line.text

                        // Loop through individual elements (words/numbers)
                        for (element in line.elements) {
                            val wordOrNumber = element.text
                            println("Found word/number: $wordOrNumber")
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle error
                e.printStackTrace()
            }
    }
}