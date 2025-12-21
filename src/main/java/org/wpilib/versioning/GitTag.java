package org.wpilib.versioning;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

public class GitTag {
    private final String name;

    private final String commitId;

    private final ZonedDateTime dateTime;

    public static GitTag fromRef(Git git, Ref ref) {
        String name = Repository.shortenRefName(ref.getName());

        try (RevWalk walk = new RevWalk(git.getRepository())) {
            RevTag rev = walk.parseTag(ref.getObjectId());
            RevObject target = walk.peel(rev);
            walk.parseBody(rev.getObject());
            String commitId = ObjectId.toString(target);

            Instant instant = rev.getTaggerIdent().getWhenAsInstant();
            ZoneId zone = rev.getTaggerIdent().getZoneId();
            if (zone == null) {
                zone = ZoneOffset.UTC;
            }
            ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, zone);
            return new GitTag(name, commitId, dateTime);
        } catch (IOException e) {
            return null;
        }
    }

    private GitTag(String name, String commitId, ZonedDateTime dateTime) {
        this.name = name;
        this.commitId = commitId;
        this.dateTime = dateTime;

    }

    public String getName() {
        return name;
    }

    public String getCommitId() {
        return commitId;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }
}
