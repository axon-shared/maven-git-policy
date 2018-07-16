package com.axondevgroup;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

/**
 * Goal which creates the git hook to enforce the commit message
 *
 * @goal commit-msg
 * @phase process-sources
 */
public class GitEnforceCommitMessageHook extends AbstractMojo
{
    private String path = ".git/hooks";
    private String hookCommitMsg = "commit-msg";

    public void execute() throws MojoExecutionException
    {
        /* Check the path exists */
        if (!new File(path).exists()) {
            getLog().info(path + " doesn't exist. Skipping hook installation");
            return;
        }

        getLog().info("Installing commit-msg hook");

        String hook = "";

        ClassLoader classLoader = getClass().getClassLoader();
        try {
            hook = IOUtils.toString(classLoader.getResourceAsStream("commit-msg"));
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }

        File targetFile = new File(path + "/" + hookCommitMsg);
        try {
            FileWriter writer = new FileWriter(targetFile);
            writer.write(hook);
            writer.close();

            Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            perms.add(PosixFilePermission.OWNER_EXECUTE);

            Files.setPosixFilePermissions(targetFile.toPath(), perms);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
