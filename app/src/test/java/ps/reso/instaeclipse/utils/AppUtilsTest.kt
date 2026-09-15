package ps.reso.instaeclipse.utils

import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalCoroutinesApi::class)
class AppUtilsTest {

    private class FakeProcess(private val exitCode: Int) : Process() {
        var destroyed: Boolean = false
            private set

        override fun waitFor(): Int = exitCode
        override fun exitValue(): Int = exitCode
        override fun destroy() {
            destroyed = true
        }

        override fun getInputStream(): InputStream = ByteArrayInputStream(ByteArray(0))
        override fun getOutputStream(): OutputStream = ByteArrayOutputStream()
        override fun getErrorStream(): InputStream = ByteArrayInputStream(ByteArray(0))
    }

    @Test
    fun forceStopInstagram_withRootGranted_returnsTrueAndDestroysProcess() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeProcess = FakeProcess(exitCode = 0)
        var capturedCmd: Array<String>? = null

        val result = AppUtils.forceStopInstagram(
            context = FakeContext(),
            packageName = "com.instagram.android",
            ioDispatcher = testDispatcher,
            processExecutor = { cmd ->
                capturedCmd = cmd
                fakeProcess
            }
        )

        assertTrue(result)
        assertArrayEquals(arrayOf("su", "-c", "am force-stop com.instagram.android"), capturedCmd)
        assertTrue(fakeProcess.destroyed)
    }

    @Test
    fun forceStopInstagram_withCustomPackage_executesForceStopOnTargetPackage() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeProcess = FakeProcess(exitCode = 0)
        var capturedCmd: Array<String>? = null

        val result = AppUtils.forceStopInstagram(
            context = FakeContext(),
            packageName = "com.instaflow.android",
            ioDispatcher = testDispatcher,
            processExecutor = { cmd ->
                capturedCmd = cmd
                fakeProcess
            }
        )

        assertTrue(result)
        assertArrayEquals(arrayOf("su", "-c", "am force-stop com.instaflow.android"), capturedCmd)
        assertTrue(fakeProcess.destroyed)
    }

    @Test
    fun forceStopInstagram_whenRootDeniedOrNonZeroExit_returnsFalse() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val fakeProcess = FakeProcess(exitCode = 1)

        val result = AppUtils.forceStopInstagram(
            context = FakeContext(),
            packageName = "com.instagram.android",
            ioDispatcher = testDispatcher,
            processExecutor = { fakeProcess }
        )

        assertFalse(result)
        assertTrue(fakeProcess.destroyed)
    }

    @Test
    fun forceStopInstagram_whenRootExecutionThrowsException_returnsFalse() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)

        val result = AppUtils.forceStopInstagram(
            context = FakeContext(),
            packageName = "com.instagram.android",
            ioDispatcher = testDispatcher,
            processExecutor = { throw IOException("su binary not found") }
        )

        assertFalse(result)
    }

    private class FakeContext : android.content.ContextWrapper(null) {
        override fun getApplicationContext(): Context = this
    }
}
