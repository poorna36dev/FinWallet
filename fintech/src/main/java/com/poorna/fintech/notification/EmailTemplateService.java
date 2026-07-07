package com.poorna.fintech.notification;

import org.springframework.stereotype.Component;
import com.poorna.fintech.dtos.TransferCompletedEvent;

@Component
public class EmailTemplateService{
    
    public String buildTransferSuccessEmail(TransferCompletedEvent event){
        String htmlBody = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
        </head>
        <body style="font-family: Arial, sans-serif; background-color:#f5f5f5; padding:30px;">

        <div style="max-width:600px; margin:auto; background:white; border-radius:10px; padding:30px; border:1px solid #dddddd;">

            <h2 style="color:#2E7D32;">Transfer Successful ✅</h2>

            <p>Hi <strong>%s</strong>,</p>

            <p>Your money transfer has been completed successfully.</p>

            <hr>

            <table style="width:100%%; border-collapse:collapse;">
                <tr>
                    <td><strong>Transaction ID</strong></td>
                    <td>%d</td>
                </tr>
                <tr>
                    <td><strong>Amount</strong></td>
                    <td>%s %s</td>
                </tr>
                <tr>
                    <td><strong>From Wallet</strong></td>
                    <td>%d</td>
                </tr>
                <tr>
                    <td><strong>To Wallet</strong></td>
                    <td>%d</td>
                </tr>
                <tr>
                    <td><strong>Transferred At</strong></td>
                    <td>%s</td>
                </tr>
            </table>

            <hr>

            <p>
                If you did not authorize this transaction,
                please contact our support team immediately.
            </p>

            <p style="margin-top:30px;">
                Regards,<br>
                <strong>Fintech Wallet Team</strong>
            </p>

        </div>

        </body>
        </html>
        """.formatted(
                event.getName(),
                event.getTransactionId(),
                event.getAmount(),
                event.getCurrency(),
                event.getSourceWalletId(),
                event.getDestinationWalletId(),
                event.getTransferredAt()
        );
        return htmlBody;
    }

    public String handleVerificationEmail(String name,String link){
        String body = """
        <!DOCTYPE html>
        <html>
        <body style="font-family:Arial,sans-serif;background:#f5f5f5;padding:30px;">

        <div style="max-width:600px;margin:auto;background:white;padding:30px;border-radius:10px;">

        <h2 style="color:#2563eb;">Verify Your Email</h2>

        <p>Hello <b>%s</b>,</p>

        <p>
        Thank you for registering with <b>Fintech</b>.
        Please verify your email address by clicking the button below.
        </p>

        <p style="text-align:center;margin:30px 0;">
        <a href="%s"
        style="background:#2563eb;
        color:white;
        padding:12px 25px;
        text-decoration:none;
        border-radius:6px;
        font-weight:bold;">
        Verify Email
        </a>
        </p>

        <p>This link expires in <b>24 hours</b>.</p>

        <hr>

        <p style="font-size:13px;color:gray;">
        If you didn't create this account, you can safely ignore this email.
        </p>

        </div>

        </body>
        </html>
        """.formatted(name, link);

        return body;
    }

    public String handlePasswordResetEmail(String name,String link){
        String body = """
        <!DOCTYPE html>
        <html>
        <body style="font-family:Arial,sans-serif;background:#f5f5f5;padding:30px;">

        <div style="max-width:600px;margin:auto;background:white;padding:30px;border-radius:10px;">

        <h2 style="color:#dc2626;">Reset Your Password</h2>

        <p>Hello <b>%s</b>,</p>

        <p>
        We received a request to reset the password for your <b>Fintech</b> account.
        Click the button below to set a new password.
        </p>

        <p style="text-align:center;margin:30px 0;">
        <a href="%s"
        style="background:#dc2626;
        color:white;
        padding:12px 25px;
        text-decoration:none;
        border-radius:6px;
        font-weight:bold;">
        Reset Password
        </a>
        </p>

        <p>
        This link will expire in <b>15 minutes</b>.
        </p>

        <p>
        If you did not request a password reset, you can safely ignore this email.
        Your password will remain unchanged.
        </p>

        <hr>

        <p style="font-size:13px;color:gray;">
        For security reasons, never share this link with anyone.
        </p>

        </div>

        </body>
        </html>
        """.formatted(name, link);
        return body;
    }

    public String handlePasswordReset(String name){
        String body = """
        <!DOCTYPE html>
        <html>
        <body style="font-family:Arial,sans-serif;background:#f5f5f5;padding:30px;">

        <div style="max-width:600px;margin:auto;background:white;padding:30px;border-radius:10px;">

        <h2 style="color:#16a34a;">Password Changed Successfully</h2>

        <p>Hello <b>%s</b>,</p>

        <p>
        Your <b>Fintech</b> account password has been changed successfully.
        </p>

        <p>
        If you made this change, no further action is required.
        </p>

        <p style="padding:15px;background:#f0fdf4;border-left:4px solid #16a34a;">
        Your account is now protected with your new password.
        </p>

        <p>
        <b>Didn't change your password?</b><br>
        If you believe this was not you, please reset your password immediately or contact our support team.
        </p>

        <hr>

        <p style="font-size:13px;color:gray;">
        This is an automated security notification. Please do not reply to this email.
        </p>

        </div>

        </body>
        </html>
        """.formatted(name);

        return body;
    }

    public String handleEmailChangeVerification(String name, String verificationLink) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family:Arial,sans-serif;background:#f5f5f5;padding:30px;">

            <div style="max-width:600px;margin:auto;background:white;padding:30px;border-radius:10px;">

            <h2 style="color:#2563eb;">Verify Your New Email Address</h2>

            <p>Hello <b>%s</b>,</p>

            <p>
            We received a request to change the email address associated with your <b>Fintech</b> account.
            </p>

            <p>
            To confirm this change, please click the button below.
            </p>

            <div style="text-align:center;margin:30px 0;">
            <a href="%s"
            style="background:#2563eb;color:white;text-decoration:none;
            padding:14px 24px;border-radius:6px;font-weight:bold;display:inline-block;">
            Verify New Email
            </a>
            </div>

            <p>
            This verification link will expire in <b>24 hours</b>.
            </p>

            <p>
            If you did not request this change, you can safely ignore this email. Your current email address will remain unchanged.
            </p>

            <hr>

            <p style="font-size:13px;color:gray;">
            This is an automated security email from Fintech. Please do not reply.
            </p>

            </div>

            </body>
            </html>
            """.formatted(name, verificationLink);
    }
}