import sys
import smtpd
import socket
import getpass
import smtplib
import asyncore
import threading

# Questo è un proxy configurabile per le email
# Ho usato il servizio SMTP di google come server di riferimento

class CustomSMTPServer(smtpd.SMTPServer):

  # https://github.com/MISP/mail_to_misp/issues/13#issuecomment-577987466 
  def process_message(self, peer, mailfrom, rcpttos, data, mail_options=None, rcpt_options=None):
    send(mailfrom, rcpttos[0], data)

# https://stackoverflow.com/a/14496815/4943299
class MyReceiver(object):

  def start(self):
    """Start the listening service"""

    host = 1025

    # show the IP Address of the fake SMTP server
    ip = socket.gethostbyname(socket.gethostname())
    print(f"SMTP Server: {ip}:{host}")

    # note the '0.0.0.0' is required
    # to be accessed in all the network devices
    # using localhost will only be accessible the host only(?)
    self.smtp = CustomSMTPServer(("0.0.0.0", host), None)

    # set timeout to 1 to prevent blocking after SMTP is closed
    self.thread =  threading.Thread(target=asyncore.loop, kwargs = {"timeout": 1} )
    self.thread.start()  

  def stop(self):
    """Stop listening now to port 1025"""

    # close the SMTPserver to ensure
    # no channels connect to asyncore
    # before waiting for the thread to finish
    self.smtp.close()
    self.thread.join()

def send(sender, receiver, data):
  """SMTP send process"""

  # can be manually set
  # but do not store email or passwords
  # on source codes
  email = "eccomerceorder@gmail.com"#input("SMTP Email Address: ")
  password = "ufuonfacrtuxgzjw"#getpass.getpass("SMTP Email Password: ")

  mailserver = smtplib.SMTP("smtp.gmail.com", 587)
  mailserver.ehlo()
  mailserver.starttls()
  mailserver.ehlo()
  mailserver.login(email, password)
  
  print("\nSending email...")
  try:
    mailserver.sendmail(sender, receiver, data.decode("utf-8"))
    print("- Email was sent.")
    print("- Sender:", sender)
    print("- Receiver:", receiver)
    mailserver.quit()
  except Exception as e:
    print("- Unable to send mail.", e)
    sys.exit(1)

# start the listener
MyReceiver().start()