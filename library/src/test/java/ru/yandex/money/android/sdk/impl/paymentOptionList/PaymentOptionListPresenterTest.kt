/*
 * The MIT License (MIT)
 * Copyright © 2018 NBCO Yandex.Money LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the “Software”), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package ru.yandex.money.android.sdk.impl.paymentOptionList

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import ru.yandex.money.android.sdk.Wallet
import ru.yandex.money.android.sdk.createAbstractWalletPaymentOption
import ru.yandex.money.android.sdk.createLinkedCardPaymentOption
import ru.yandex.money.android.sdk.createNewCardPaymentOption
import ru.yandex.money.android.sdk.createWalletPaymentOption
import ru.yandex.money.android.sdk.impl.extensions.initExtensions
import ru.yandex.money.android.sdk.payment.loadOptionList.PaymentOptionListOutputModel
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [27])
class PaymentOptionListPresenterTest {

    @[Rule JvmField]
    val timeout = Timeout(1, TimeUnit.MINUTES)

    @Before
    fun setUp() {
        initExtensions(RuntimeEnvironment.application)
    }

    @Test
    fun testNonePaymentOptions() {
        // prepare
        val presenter = PaymentOptionListPresenter(RuntimeEnvironment.application, false)
        val outputModel: PaymentOptionListOutputModel = emptyList()

        // invoke
        val viewModel = presenter(outputModel)

        // assert
        assertThat("should be empty", viewModel.paymentOptions.isEmpty())
    }

    @Test
    fun testShowLogo() {
        // prepare
        val showLogo = true
        val presenter = PaymentOptionListPresenter(RuntimeEnvironment.application, showLogo)

        // invoke
        val viewModel = presenter(emptyList())

        // assert
        assertThat("should show logo", viewModel.showLogo)
    }

    @Test
    fun testNoLogo() {
        // prepare
        val showLogo = false
        val presenter = PaymentOptionListPresenter(RuntimeEnvironment.application, showLogo)

        // invoke
        val viewModel = presenter(emptyList())

        // assert
        assertThat("should not show logo", !viewModel.showLogo)
    }

    @Test
    fun testAllPaymentOptions() {
        // prepare
        val presenter = PaymentOptionListPresenter(RuntimeEnvironment.application, false)
        val outputModel = listOf(
                createNewCardPaymentOption(0),
                createWalletPaymentOption(1),
                createAbstractWalletPaymentOption(2),
                createLinkedCardPaymentOption(3))

        // invoke
        val viewModel = presenter(outputModel)

        // assert
        assertThat("should be same length", viewModel.paymentOptions.size, CoreMatchers.equalTo(outputModel.size))
        outputModel.zip(viewModel.paymentOptions).forEach { (model, viewModel) ->
            val modelName = model.toString()
            assertThat(modelName, viewModel.optionId == model.id)
            assertThat(modelName, viewModel.canLogout, equalTo(model is Wallet))
            assertThat(modelName, viewModel.additionalInfo == null, equalTo(model !is Wallet))
        }
    }
}