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

    public GitTag(Git git, Ref ref) {
        name = Repository.shortenRefName(ref.getName());

        try (RevWalk walk = new RevWalk(git.getRepository())) {
            RevTag rev = walk.parseTag(ref.getObjectId());
            RevObject target = walk.peel(rev);
            walk.parseBody(rev.getObject());
            commitId = ObjectId.toString(target);

            Instant instant = rev.getTaggerIdent().getWhenAsInstant();
            ZoneId zone = rev.getTaggerIdent().getZoneId();
            if (zone == null) {
                zone = ZoneOffset.UTC;
            }
            dateTime = ZonedDateTime.ofInstant(instant, zone);
        } catch (IOException e) {
            throw new RuntimeException("Error reading git tag " + name, e);
        }
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
