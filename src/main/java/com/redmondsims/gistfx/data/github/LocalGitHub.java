package com.redmondsims.gistfx.data.github;

import com.redmondsims.gistfx.data.GistFileId;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;

import java.util.concurrent.ConcurrentHashMap;

public class LocalGitHub {


	private ConcurrentHashMap<String, GHGist>         ghGistMap     = null;
	private ConcurrentHashMap<GistFileId, GHGistFile> ghGistFileMap = new ConcurrentHashMap<>();


}
