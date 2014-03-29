# Include the Dropbox SDK
import dropbox
import time
import os

# Get your app key and secret from the Dropbox developer website
app_key = 'yora28jjyc8zk96'
app_secret = 'pxbybhgouwhc1yt'

flow = dropbox.client.DropboxOAuth2FlowNoRedirect(app_key, app_secret)
authorize_url = flow.start()

# Have the user sign in and authorize this token
authorize_url = flow.start()
print '1. Go to: ' + authorize_url
print '2. Click "Allow" (you might have to log in first)'
print '3. Copy the authorization code.'
code = raw_input("Enter the authorization code here: ").strip()

# This will fail if the user enters an invalid authorization code
access_token, user_id = flow.finish(code)

client = dropbox.client.DropboxClient(access_token)
print 'linked account: ', client.account_info()

uploaded=set()
while True:
    time.sleep(2)
    fs=os.listdir('F:\\bbgdata\\BBGTick')
    for i in fs:
        if i[0:5]=='done.':
            fname=i[5:]
            fp='BBGTick\\'+fname
            print "processing",fp
            fp2='BBGTick\\done.'+fname
            f = open(fp, 'rb')
            response = client.put_file('BBG/'+fname, f)
            f.close()
            os.rename('F:\\bbgdata\\'+fp,'F:\\uploaded\\'+fname)
            os.remove(fp2)
            print "uploaded:", response

