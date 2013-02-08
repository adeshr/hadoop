/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.namenode;

import java.io.PrintWriter;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.server.namenode.snapshot.Snapshot;

/**
 * An {@link INode} representing a symbolic link.
 */
@InterfaceAudience.Private
public class INodeSymlink extends INode {
  private final byte[] symlink; // The target URI

  INodeSymlink(long id, String value, long mtime, long atime,
      PermissionStatus permissions) {
    super(id, permissions, mtime, atime);
    this.symlink = DFSUtil.string2Bytes(value);
  }
  
  INodeSymlink(INodeSymlink that) {
    super(that);

    //copy symlink
    this.symlink = new byte[that.symlink.length];
    System.arraycopy(that.symlink, 0, this.symlink, 0, that.symlink.length);
  }

  @Override
  INode recordModification(Snapshot latest) {
    return parent.saveChild2Snapshot(this, latest, new INodeSymlink(this));
  }

  /** @return true unconditionally. */
  @Override
  public boolean isSymlink() {
    return true;
  }

  public String getSymlinkString() {
    return DFSUtil.bytes2String(symlink);
  }

  public byte[] getSymlink() {
    return symlink;
  }

  @Override
  DirCounts spaceConsumedInTree(DirCounts counts) {
    counts.nsCount += 1;
    return counts;
  }
  
  @Override
  int destroySubtreeAndCollectBlocks(final Snapshot snapshot,
      final BlocksMapUpdateInfo collectedBlocks) {
    return 1;
  }

  @Override
  long[] computeContentSummary(long[] summary) {
    summary[1]++; // Increment the file count
    return summary;
  }

  @Override
  public void dumpTreeRecursively(PrintWriter out, StringBuilder prefix,
      final Snapshot snapshot) {
    super.dumpTreeRecursively(out, prefix, snapshot);
    out.println();
  }
}
