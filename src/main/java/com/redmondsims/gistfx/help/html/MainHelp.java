package com.redmondsims.gistfx.help.html;

public class MainHelp {
	public static final String html = """
			<p>GistFX aims to do one thing - provide you with an application that makes creating and editing Gists as easy and simple as possible.</p>

			<p>&nbsp;</p>

			<p>The first time it launches and successfully authenticates to GitHub with your personal access token, it downloads all of your Gists and files and stores them in a local SQLite database. It then changes a setting so that every time you
			    launch GistFX in the future, it loads everything from the local database after authenticating to GitHub. HOWEVER, after the UI is loaded in subsequent launches of GistFX, it will re-download your Gists from GitHub in the background. You
			    can see the progress of this download in the progress bar that shows in the main window. DO NOT make any changes to your Gists that would require uploading to GitHub while this download is happening.</p>

			<p>&nbsp;</p>

			<p>&nbsp;</p>

			<p>There are five actions you can make, which will immediately commit to GitHub:</p>

			<p>&nbsp;</p>

			<ol>
			    <li>Changing your Gist description.</li>
			    <li>Changing the Gist state from Public to Private or back.</li>
			    <li>Changing the name of a file.</li>
			    <li>Creating a New Gist</li>
			    <li>Creating a New File</li>
			</ol>

			<p>You should not attempt to do any of those actions while the background download is in progress. You can, however, edit any of your Gists files.</p>

			<p>&nbsp;</p>

			<p>The reason GistFX downloads your Gists each time you run the program is that the GitHub API provides access to your Gists through those objects that are downloaded. So they are necessary when committing changes to your GitHub
			    account.</p>

			<p>&nbsp;</p>

			<h2>Editing Files</h2>

			<p><strong>Editing your Gists files does not immediately commit those changes to GitHub</strong>. Your edits are continually saved in the local database and are marked with a red dot when they are considered &quot;dirty&quot;, which simply
			    means they have not yet been uploaded to GitHub.</p>

			<p>&nbsp;</p>

			<p><img alt="" src="~~File1~~" style="height:102px; width:400px"/></p>

			<p>&nbsp;</p>

			<p>&nbsp;</p>

			<p>You can upload your changes at any time, simply by clicking on <strong>Save File</strong> in the File menu (or on the button bar), while that file is active. A file becomes active when you click on it in the tree. If you have multiple
			    files that have not yet been saved, you can save them all at once by clicking on <strong>Save All Files</strong> in the File menu.</p>

			<p>&nbsp;</p>

			<p>&nbsp;</p>

			<p>When you exit GistFX, if you have changes that have not yet been uploaded, you will be prompted and notified about the unsaved data and given an opportunity to save it before the program closes.</p>

			<p>&nbsp;</p>

			<h2>Naming Gists</h2>

			<p>GistFX gives you the ability to give your Gists names and not just descriptions. This can be useful when trying to organize your Gists. It will also make the tree much cleaner when you give shorter names to your Gists. GistFX will, by
			    default, assign the description to the name until you change it. Also, GistFX stores the names in a table in the database that does not get wiped out with new or refresh downloads of your Gists. When you chose to re-download your Gists,
			    GistFX will re-assign any names you have previously given to your Gists.</p>

			<p>&nbsp;</p>

			<p>Each time GistFX creates the tree during the loading of the UI, it will sort your Gists alphabetically by their given name.</p>

			<p>&nbsp;</p>

			<h2>Changing Key Gist Tags</h2>

			<p>You can, of course, change your Gists description and the name of any given file. To change the description, simply click on a Gist in the tree, then click anywhere on the description and you will be shown a window where you can then
			    edit the description. When you are done and you save that edit, it will immediately be uploaded to GitHub. You change the names of your files in the same way. Click on a file so that it is active, then simply click anywhere on the file
			    name. When you save that change, it too will be immediately uploaded to GitHub.</p>

			<h2>Private / Public</h2>

			<p>Changing a Gist from Private to Public is not a trivial matter. The reason for this is because Gists can have forks and so it is not functionally trivial to simply switch its state from Public to Private and back. However, if you desire
			    to do this, GistFX can accommodate. The process is straightforward. When you click on the check box to change the state of your Gist, you are given a warning box explaining everything in detail along with the number of forks that your
			    Gist has, if any. If you proceed with the change, GistFX will first delete the Gist, then create a new one, and then it will populate it with your files. This, too, is immediately uploaded to GitHub.</p>

			<p>&nbsp;</p>

			<p>The forks that were connected to your Gist will simply become local copies for those users who owned a fork. They will not be able to pull any future changes that you make to the Gist if you do change its public/private state.</p>

			<p>&nbsp;</p>

			<h2>Re-Downloading Gists</h2>

			<p>Under the Gist menu is an option to Download Gists. When you click on this menu item, GistFX will wipe out all of the local data and it will re-download a new copy from GitHub. If you have changes that have not yet been uploaded, those
			    changes will be lost if you do this.</p>

			<h2>Creating a new file</h2>

			<p>In order to create a new file in a Gist, you must first click on the Gist in the tree, then click on New File in the File Menu (or on the button bar). This will pop up a window where you need to type in the name of the new file. GistFX
			    will then create the new file in your Gist and will populate it with some sample code and it will then post the new file to your GitHub account.</p>

			<h2>Button Bar</h2>

			<p><img alt="" src="~~File2~~" style="height:50px; width:650px"/></p>

			<p>Clicking on <strong>Show ButtonBar</strong> in the View menu will reveal a Button bar just above the code editing window. These buttons are likely to be the most often used actions you will perform while working in GistFX. You can also
			    <strong>Hide ButtonBar</strong> from the View menu once it is shown.</p>
			</body>""";
}
