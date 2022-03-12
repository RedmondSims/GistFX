package com.redmondsims.gistfx.help.html;

public class CreateToken {
	public static final String html = """
			<h3 style="text-align:center">How To Create A Personal Access Token</h3>

			<p>1) Log in to your GitHub account with a web browser.</p>

			<p>2) In the upper right corner of the web page you will see your avatar with a down arrow next to it, click on the down arrow, then click on Settings.</p>
			<br>
			<p><img alt="" src="~~File1~~" style="height:216px; width:200px"/></p>
			<br>
			<p>3) Click on Developer Settings</p>
			<br>
			<p><img alt="" src="~~File2~~" style="height:141px; width:350px"/></p>
			<br>
			<p>4) Click on Personal Access Tokens</p>
			<br>
			<p><img alt="" src="~~File3~~" style="height:200; width:450"/></p>
			<br>
			<p>5) Click on Generate New Token</p>
			<br>
			<p><img alt="" src="~~File4~~" style="height:214px; width:450px"/></p>
			<br>
			<p>6) Give then token a name</p>
			<br>
			<p><img alt="" src="~~File5~~" style="height:186px; width:450px"/></p>
			<br>
			<p>7) Next, you only need to click on <strong>ONE BOX</strong></p>
			<br>
			<p><img alt="" src="~~File6~~" style="height:161px; width:450px"/></p>
			<br>
			<p>8) Click on the icon that will copy the token to your clipboard</p>
			<br>
			<p><img alt="" src="~~File7~~" style="height:150px; width:450px"/></p>
			<br>
			<p>9) Paste the token into the token field in the login screen.</p>
			<br>
			<br>
			<p>Once you have pasted the token in the login screen, you are automatically given the ability to type in a password that you will need each time you run GistFX. You can, however, uncheck the 'Save Access Token' chceck box where you can
			    then simply log in with your token only. However, you will need to paste in your token each time you run GistFX. Your password is first hashed with an algorithm, and that hash is stored locally. The hash cannot be easily cracked as it
			    uses the latest methods of hashing and those APIs indicate that it would take almost 1,000 years at modern compter speeds to brute force crack your password. Next, your password is then used to generate an encryption key, which is used
			    throughout your session to encrypt and decrypt data that is stored locally, including your personal access token.</p>
			<br>
			<p>When you then log into GistFX after saving your token, GistFX will first verify that your password is correct by checking its hash with the hash stored locally and if the API says it's a match, it then uses the cleartext password you
			    typed in to generate the encryption key for use during your session. Finally, it decrypts the local copy of your personal access token and passes it via another API to GitHub and if it is validated, GistFX downloads your Gists and
			    stores them locally - and uses a quasi-online approach to syncing your data with your GitHub account. See the help menu for more information about that.&nbsp;</p>
			<br>
			<p>The login window provides information about where GistFX is in the authentication process and when it downloads your data from GitHub, there is a progress bar that will keep moving as the download happens. The progress bar value is
			    calculated based on the number of gists and files that you have in your account, so it updates one time after each file has been downloaded. If you happen to have any files that are extremely large, the progress bar will not move while
			    that file is downloading. Be aware of that so that it is not assumed that something is wrong when the progress bar hasn't updated in a while.</p>
			</body></html>
			""";
}
