![Login image](./img/GistFXLogo.png)

Forget using your default text editor application to jot down pieces of
code. Why not store these, instead, in a version controlled format?

GitHub Gists are a great way to store small code snippets, or even small
projects. GistFX provides a desktop Gist client, to make editing, creating, and
managing Gists a breeze.

### Features
  - Built-in syntax highlighting for most  languages, using MonacoFX  - the same code editor used in Microsofts open source Visual Studio.
  - Easily manage existing Gists, edit, or create new ones.
  - Easily create new files within your Gists.
  - Gists are downloaded and accessible via a JavaFX TreeView.
  - Assign names to your Gists in addition to the descriptions that GitHub requires.
  - Authenticate with a GitHub access token, that can be saved locally in an encrypted format.
  - Flags edited files that need to be uploaded to GitHub and you decide when to push your edits.
  - Wide Mode: Maximize screen space for code editing.
  - Distraction Free Mode - Just the code editor in full screen.

### Coming soon
  - Categories: Group your gists into categories that you create.
  - Keyboard shortcuts for mouse free navigation
### Getting started

  1. Create a GitHub Personal Access Token for this application
     - This is far superior to user/pass authentication and is the only way GitHub allows access to your data.
     - Only gives GistFX the ability to access your Gists through the GitHub API.
       - The full instructions with screenshots are available when you run GistFX by clicking on the question mark at the login screen. OR
       - Create by [clicking here](https://github.com/settings/tokens/new)
         - Check ONE box - **Gist**
    
  2. Running GistFX
     - Currently, GistFX only runs within the IDE or by using Maven. This will be resolved soon. If you are able to change the Maven POM file so that it generates a working artifact, then please submit a pull request. Though we expect to have this working soon.
     - Here is how I set up the environment so that it will run within IntelliJ, but I'm sure these steps can be used to make it work in any IDE.
        - Install Java JDK 17.0.1 or higher.
        - Download the matching version of JavaFX 17 **SDK** and **jmods** for your operating system [using this link](https://gluonhq.com/products/javafx/)
        - Unzip those files.
        - Follow the folder structure and merge those files into the folders where your core JDK is installed, overwriting any files with the same name.
        - Set your IDE to use the Java 17 JDK that you installed.
          - Run Main.java from inside your IDE OR
          - From the command line, go into the root of the project folder
            - Make sure your environment paths are set for the right JDK. I use [jenv](https://www.jenv.be/) for this because it's SIMPLE and POWERFUL
            - Execute: **mvn clean javafx:run**
---

### On first run

You will first be shown a window that explains the need for an access token, this window only shows on the first run.

Next, you will be presented with a login screen, click on the question mark for detailed instructions for creating a proper access token.

 ![Login image](./img/GistFX-Login.png) 

Enter the GitHub personal access token that you created, along with a password that will be used to encrypt then locally store your personal access token.

If you uncheck the Save Token checkbox, you will be able to authenticate by simply entering your personal access token. However, each time you run GistFX, you will need to enter your token again.

---

Once you authenticate successfully to your GitHub account, your Gists are download and stored inside a SQLite database. The data is first encrypted before it is stored for your protection. Then, any changes you make to your gists will be kept in the database until you upload them. Your changes are not uploaded to GitHub automatically, in order to reduce the number of version changes that accumulate in your Gists.

GistFX makes it very clear when you have data that needs to be committed to your GitHub account, and it will present you with an opportunity to commit those changes when you close the program or when you open the program if you have uncommitted data in the local database.

See the Help menu for further discussion concerning the use and utility of GistFX. We think you will find this program to be very handy in the preservation of those code snippets that matter the most in your development endeavours. We not only develop this program, we use it regularly, so we do everything we can to make it a tool that is both easy to use as a relevant addition to our arsenal of development tools.