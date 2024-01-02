package net.yupol.transmissionremote.app.transport

import com.octo.android.robospice.persistence.exception.SpiceException
import net.yupol.transmissionremote.app.transport.request.ResponseFailureException

val SpiceException.responseFailureMessage: String?
    get() = (this.cause as? ResponseFailureException)?.failureMessage
