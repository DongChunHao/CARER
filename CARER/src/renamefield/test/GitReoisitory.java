package renamefield.test;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitReoisitory {
	public static boolean hasSwitchedToCommit(String repositoryPath, String commitId) throws IOException, NoHeadException, GitAPIException {
        Repository repository = new FileRepositoryBuilder()
                .setGitDir(new File(repositoryPath))
                .build();
        Git git = new Git(repository);
        LogCommand logCommand = git.log();
        ObjectId targetCommitId = repository.resolve(commitId);
        Iterable<RevCommit> commits = git.log().call();
        for (RevCommit commit : commits) {
            if (commit.getId().equals(commitId)) {
            	git.close();
                return true;
            }
        }
        git.close();
        return false;
    }
}
