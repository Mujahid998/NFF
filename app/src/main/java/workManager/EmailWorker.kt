package workManager

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.work.Worker
import androidx.work.WorkerParameters
import emailSender.GMailSender
import utils.Constants
import utils.HelperMethods.Companion.toast

class EmailWorker(val appContext: Context, workerParams: WorkerParameters):
       Worker(appContext, workerParams) {

    private val senderEmail="nffwebmail@gmail.com"
    private val senderPassword="NFF123456"
    private val officeEmail="nff@naturallyfarmedfoods.com"

   override fun doWork(): Result {


       val fileWithPrice = inputData.getString(Constants.KEY_ATTACH_FILE_WITH_PRICE)
       val file = inputData.getString(Constants.KEY_ATTACH_FILE)
       val customerEmail = inputData.getString(Constants.KEY_CUSTOMER_EMAIL)
       val customerName = inputData.getString(Constants.KEY_CUSTOMER_NAME)




       try {
           val sender = GMailSender(senderEmail, senderPassword)

           customerEmail?.let { customerEmail ->
               sender.sendMailToClient(
                       fileWithPrice,
                       "Food order for ($customerName)",
                       "Hi $customerName, Order receipt is attached.",
                       senderEmail,
                       customerEmail
               )
           }

           sender.sendMailToOffice(
                   file,
                   fileWithPrice,
                   "Food order for ($customerName)",
                   "Both receipts are attached.",
                   senderEmail,
                   officeEmail
           )


           return Result.success()

       } catch (e: Exception) {
           Log.e("NFFEmail", e.message, e)
           appContext.toast(e.message.toString())
           return Result.failure()
       }







   }
}
