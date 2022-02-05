package com.example.demo

import com.xuggle.mediatool.IMediaWriter
import com.xuggle.mediatool.ToolFactory
import com.xuggle.xuggler.ICodec
import java.awt.*
import java.awt.image.BufferedImage
import java.util.concurrent.TimeUnit


object RecVideo {
    private const val FRAME_RATE = 25.0
    private const val outputFilename = "mydesktop.mp4"
    private var screenBounds: Dimension? = null
    @JvmStatic
    fun run(SECONDS_TO_RUN_FOR : Boolean) {
        val writer: IMediaWriter = ToolFactory.makeWriter(outputFilename)
        screenBounds = Toolkit.getDefaultToolkit().screenSize
        writer.addVideoStream(
            0, 0, ICodec.ID.CODEC_ID_MPEG4,
            1920, 1080
        )
        val startTime = System.nanoTime()
        while (SECONDS_TO_RUN_FOR) {

            val screen = desktopScreenshot

            val bgrScreen = convertToType(
                screen,
                BufferedImage.TYPE_3BYTE_BGR
            )
            writer.encodeVideo(
                0, bgrScreen, System.nanoTime() - startTime,
                TimeUnit.NANOSECONDS
            )
            try {
                Thread.sleep((1000 / FRAME_RATE).toLong())
            } catch (e: InterruptedException) {

                // ignore
            }
        }
        writer.close()
    }

    fun convertToType(sourceImage: BufferedImage?, targetType: Int): BufferedImage? {
        val image: BufferedImage?
        if (sourceImage!!.type == targetType) {
            image = sourceImage
        } else {
            image = BufferedImage(
                sourceImage.width,
                sourceImage.height, targetType
            )
            image.graphics.drawImage(sourceImage, 0, 0, null)
        }
        return image
    }

    private val desktopScreenshot: BufferedImage?
        private get() = try {
            val robot = Robot()
            val captureSize = Rectangle(screenBounds)
            robot.createScreenCapture(captureSize)
        } catch (e: AWTException) {
            e.printStackTrace()
            null
        }
}