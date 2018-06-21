package net.yupol.transmissionremote.utils

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class LiveDataExtTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testMap() {
        val ints = MutableLiveData<Int>()

        val squareInts = ints.map { n ->
            n ?: return@map null
            n * n
        }

        val observer = mock<Observer<Int?>>()
        squareInts.observeForever(observer)

        ints.value = 2
        assertThat(squareInts.value).isEqualTo(4)

        ints.value = null
        assertThat(squareInts.value).isEqualTo(null)

        ints.value = 3
        assertThat(squareInts.value).isEqualTo(9)

        ints.value = 4
        assertThat(squareInts.value).isEqualTo(16)

        verify(observer, times(4)).onChanged(any())
    }

    @Test
    fun testMapSkipNulls() {
        val ints = MutableLiveData<Int>()

        val squareInts = ints.mapSkipNulls { n ->
            n * n
        }

        val observer = mock<Observer<Int?>>()
        squareInts.observeForever(observer)

        ints.value = 2
        assertThat(squareInts.value).isEqualTo(4)

        ints.value = null
        assertThat(squareInts.value).isEqualTo(4)

        ints.value = 3
        assertThat(squareInts.value).isEqualTo(9)

        ints.value = 4
        assertThat(squareInts.value).isEqualTo(16)

        verify(observer, times(3)).onChanged(any())
    }

    @Test
    fun testCombineLatest() {
        val ints = MutableLiveData<Int>()
        val chars = MutableLiveData<Char>()
        val pairs = ints.combineLatest(chars)
        pairs.observeForever {}

        ints.value = 1
        assertThat(pairs.value).isNull()
        chars.value = 'A'
        assertThat(pairs.value).isEqualTo(1 to 'A')
        ints.value = 2
        assertThat(pairs.value).isEqualTo(2 to 'A')
        chars.value = 'B'
        assertThat(pairs.value).isEqualTo(2 to 'B')
    }

    @Test
    fun testCombineLatestClearOnNull() {
        val ints = MutableLiveData<Int>()
        val chars = MutableLiveData<Char>()
        val pairs = ints.combineLatest(chars)
        pairs.observeForever {}

        ints.value = 1
        assertThat(pairs.value).isEqualTo(null)
        chars.value = 'A'
        assertThat(pairs.value).isEqualTo(1 to 'A')
        ints.value = null
        assertThat(pairs.value).isNull()
        ints.value = 2
        assertThat(pairs.value).isEqualTo(2 to 'A')
    }

    @Test
    fun testCombineLatestCallCount() {
        val ints = MutableLiveData<Int>()
        val chars = MutableLiveData<Char>()
        val pairs = ints.combineLatest(chars)
        val observer = mock<Observer<Pair<Int, Char>>>()
        pairs.observeForever(observer)

        ints.value = 1
        chars.value = 'A'
        ints.value = null
        chars.value = null
        ints.value = 2
        chars.value = 'B'

        verify(observer, times(1)).onChanged(1 to 'A')
        verify(observer, times(1)).onChanged(null)
        verify(observer, times(1)).onChanged(2 to 'B')
    }

    private inline fun <reified T: Any> mock(): T = Mockito.mock(T::class.java)
}