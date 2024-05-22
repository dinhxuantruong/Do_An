import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class FirebaseListenerObserver(
    private val dbReference: Query,
    private val valueEventListener: ValueEventListener
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        dbReference.removeEventListener(valueEventListener)
    }

}
