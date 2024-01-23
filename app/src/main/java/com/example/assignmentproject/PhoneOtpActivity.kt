import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.assignmentproject.MapsActivity
import com.example.assignmentproject.databinding.ActivityPhoneOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class PhoneOtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneOtpBinding
    private val mAuth = FirebaseAuth.getInstance()
    private var storedVerificationId: String? = null
    private lateinit var forceResndToken: ForceResendingToken
    private var timeoutSeconds = 60L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resendOTPButton.setOnClickListener {
            sendOtp(binding.phoneNoText.text.toString(), true)
        }

        binding.sendOTPButton.setOnClickListener {
            sendOtp(binding.phoneNoText.text.toString(), false)
        }

        binding.checkOTPButton.setOnClickListener {
            val otp = binding.otpText.text.toString()
            if (storedVerificationId != null) {
                val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, otp)
                signIn(credential)
            } else {
                // Handle the case where storedVerificationId is not initialized
                Toast.makeText(applicationContext, "Verification ID not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendOtp(phoneNumber: String?, isResend: Boolean) {
        startResendTimer()
        setInProgress(true)
        val builder = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber!!)
            .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    signIn(phoneAuthCredential)
                    setInProgress(false)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(applicationContext, "OTP verification failed", Toast.LENGTH_SHORT).show()
                    setInProgress(false)
                }

                override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                    super.onCodeSent(s, forceResendingToken)
                    storedVerificationId = s
                    forceResndToken = forceResendingToken
                    Toast.makeText(applicationContext, "OTP successfully sent", Toast.LENGTH_SHORT).show()
                    setInProgress(false)
                }
            })
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(forceResndToken).build())
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build())
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.progressBarSignUp.visibility = View.VISIBLE
            binding.resendOTPButton.visibility = View.GONE
        } else {
            binding.progressBarSignUp.visibility = View.GONE
            binding.resendOTPButton.visibility = View.VISIBLE
        }
    }

    private fun signIn(phoneAuthCredential: PhoneAuthCredential) {
        setInProgress(true)
        mAuth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener { task ->
                setInProgress(false)
                if (task.isSuccessful) {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "OTP verification failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun startResendTimer() {
        binding.resendOTPButton.isEnabled = false
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                timeoutSeconds--
                binding.resendOTPButton.text = "Resend OTP in $timeoutSeconds seconds"
                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L
                    timer.cancel()
                    runOnUiThread { binding.resendOTPButton.isEnabled = true }
                }
            }
        }, 0, 1000)
    }
}
