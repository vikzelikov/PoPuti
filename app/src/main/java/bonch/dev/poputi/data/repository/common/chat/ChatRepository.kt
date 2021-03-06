package bonch.dev.poputi.data.repository.common.chat

import android.util.Log
import bonch.dev.poputi.domain.utils.Constants
import bonch.dev.poputi.domain.utils.NetworkUtil
import bonch.dev.poputi.App
import bonch.dev.poputi.data.network.common.ChatService
import bonch.dev.poputi.domain.entities.common.chat.Message
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionState
import com.pusher.client.util.HttpAuthorizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ChatRepository : IChatRepository {

    private var service: ChatService = App.appComponent
        .getNetworkModule()
        .create(ChatService::class.java)

    private var channel: PrivateChannel? = null
    private var pusher: Pusher? = null


    override fun sendMessage(message: Message, token: String, callback: SuccessHandler) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.sendMessage(headers, message)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(true)
                    } else {
                        //Error
                        Log.e(
                            "SEND_MESSAGE",
                            "Send message to server failed with code: ${response.code()}"
                        )
                        callback(false)
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("SEND_MESSAGE", "${err.printStackTrace()}")
                callback(false)
            }
        }
    }


    override fun getMessages(rideId: Int, callback: DataHandler<ArrayList<Message>?>) {
        var response: Response<ArrayList<Message>>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                response = service.getMessages(rideId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val messages = response.body()
                        if (messages != null) callback(messages, null)
                        else callback(null, "NPE messages")
                    } else {
                        //Error
                        Log.e(
                            "GET_MESSAGES",
                            "Get messages from server failed with code: ${response.code()}"
                        )
                        callback(null, "${response.code()}")
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("GET_MESSAGES", "${err.printStackTrace()}")
                callback(null, err.message)
            }
        }
    }


    override fun connectSocket(
        rideId: Int,
        token: String,
        callback: SuccessHandler
    ) {
        val log = "CHAT_SOCKET/P"

        val options = PusherOptions()
        options.setCluster("mt1")
        val auth = HttpAuthorizer(Constants.BASE_URL.plus("/broadcasting/auth"))
        auth.setHeaders(NetworkUtil.getHeaders(token))
        options.authorizer = auth

        if (pusher?.connection?.state != ConnectionState.CONNECTED) {

            pusher = Pusher(Constants.API_KEY_PUSHER, options)

            pusher?.connect()

            channel = pusher?.subscribePrivate("private-ride.$rideId.chat",
                object : PrivateChannelEventListener {
                    override fun onEvent(event: PusherEvent?) {}

                    override fun onAuthenticationFailure(mess: String?, e: java.lang.Exception?) {
                        Log.e(log, String.format("SOCKET CONNECT FAIL [%s], [%s]", mess, e))
                        callback(false)
                    }

                    override fun onSubscriptionSucceeded(channelName: String?) {
                        Log.i(log, "SOCKET CONNECT SUCCESS")
                        callback(true)
                    }
                })
        } else callback(true)
    }


    override fun subscribeOnChat(callback: DataHandler<String?>) {
        val event = "App\\Events\\CreateMessage"

        channel?.bind(event, object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                if (event != null) {
                    callback(event.data, null)
                } else {
                    callback(null, "Error")
                }
            }

            override fun onAuthenticationFailure(message: String?, e: java.lang.Exception?) {}

            override fun onSubscriptionSucceeded(channelName: String?) {}
        })
    }


    override fun disconnectSocket() {
        pusher?.disconnect()
    }
}