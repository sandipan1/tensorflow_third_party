/*
 * Copyright 2016 The Closure Rules Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.javascript.jscomp;

import static com.google.common.base.Preconditions.checkArgument;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.base.Optional;
import com.google.protobuf.TextFormat;
import io.bazel.rules.closure.BuildInfo.ClosureJsLibrary;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class JsCheckerHelper {

  static boolean isGeneratedPath(String path) {
    return path.startsWith("blaze-out/")
        || path.startsWith("bazel-out/");
  }

  static Optional<String> convertPathToModuleName(String path, Iterable<String> roots) {
    checkArgument(!path.startsWith("/"));
    if (!path.endsWith(".js")) {
      return Optional.absent();
    }
    String module = path.substring(0, path.length() - 3);
    for (String root : roots) {
      if (module.startsWith(root + "/")) {
        module = module.substring(root.length() + 1);
        break;
      }
    }
    return Optional.of(module);
  }

  static String normalizeClosureNamespace(String namespace) {
    return "goog:" + namespace;
  }

  /** Loads pbtxt info file generated by closure_js_library. */
  static ClosureJsLibrary loadClosureJsLibraryInfo(Path path) throws IOException {
    ClosureJsLibrary.Builder build = ClosureJsLibrary.newBuilder();
    TextFormat.getParser().merge(
        new String(Files.readAllBytes(path), UTF_8),
        build);
    return build.build();
  }

  /** Returns {@code true} if error was produced by code generated by compiler passes. */
  static boolean isInSyntheticCode(JSError error) {
    return error.sourceName != null
        && error.sourceName.startsWith(" [synthetic:");
  }

  private JsCheckerHelper() {}
}