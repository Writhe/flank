package ftl.gc

import com.google.api.services.testing.model.AndroidInstrumentationTest
import com.google.api.services.testing.model.FileReference
import ftl.args.AndroidArgs
import ftl.args.ShardChunks
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class UtilsKtTest {

    @Test
    fun `setupTestTargets should setup testTargets`() {
        // given
        val args = mockk<AndroidArgs> {
            every { disableSharding } returns true
        }
        val testShards: ShardChunks = emptyList()

        // when
        val actual = AndroidInstrumentationTest().setupTestTargets(args, testShards)
            .testTargets

        // then
        assertNotNull(actual)
    }

    @Test
    fun `setupTestTargets should setup uniformSharding`() {
        // given
        val expectedTestTargets = emptyList<String>()
        val args = mockk<AndroidArgs> {
            every { disableSharding } returns false
            every { numUniformShards } returns 50
        }
        val testShards: ShardChunks = listOf(expectedTestTargets)

        // when
        val actual = AndroidInstrumentationTest()
            .setTestApk(FileReference().setGcsPath("testApk"))
            .setupTestTargets(args, testShards)

        // then
        assertEquals(0, actual.shardingOption.uniformSharding.numShards)
        assertEquals(expectedTestTargets, actual.testTargets)
    }

    @Test
    fun `setupTestTargets should setup manualSharding`() {
        // given
        val shardChunks: ShardChunks = listOf(emptyList(), emptyList())
        val args = mockk<AndroidArgs> {
            every { disableSharding } returns false
            every { numUniformShards } returns null
        }

        // when
        val actual = AndroidInstrumentationTest().setupTestTargets(args, shardChunks)
            .shardingOption
            .manualSharding
            .testTargetsForShard

        // then
        assertEquals(shardChunks.size, actual.size)
    }
}
