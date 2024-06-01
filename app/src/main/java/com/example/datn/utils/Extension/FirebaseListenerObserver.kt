import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class FirebaseListenerObserver(
    private val databaseReference: Query,
    private val valueEventListener: ValueEventListener
) : DefaultLifecycleObserver {

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        databaseReference.removeEventListener(valueEventListener)
    }
}
