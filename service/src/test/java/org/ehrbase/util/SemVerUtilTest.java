/*
 * Copyright (c) 2024 vitasystems GmbH.
 *
 * This file is part of project EHRbase
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehrbase.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

class SemVerUtilTest {

    @Test
    void determineVersionFromRelease() {
        SemVer req = SemVer.parse("1.2.3");
        assertThat(SemVerUtil.determineVersion(req, SemVer.NO_VERSION)).isEqualTo(SemVer.parse("1.2.3"));
        assertThatExceptionOfType(VersionConflictException.class)
                .isThrownBy(() -> SemVerUtil.determineVersion(req, req));
    }

    @Test
    void determineVersionSnapshot() {
        SemVer req = SemVer.parse("1.2.3-SNAPSHOT");
        assertThat(SemVerUtil.determineVersion(req, SemVer.NO_VERSION)).isEqualTo(SemVer.parse("1.2.3-SNAPSHOT"));
        assertThat(SemVerUtil.determineVersion(req, SemVer.parse("1.2.3-SNAPSHOT")))
                .isEqualTo(SemVer.parse("1.2.3-SNAPSHOT"));
    }

    @Test
    void determineVersionAuto() {
        assertThat(SemVerUtil.determineVersion(SemVer.NO_VERSION, SemVer.NO_VERSION))
                .isEqualTo(SemVer.parse("1.0.0"));
        assertThat(SemVerUtil.determineVersion(SemVer.NO_VERSION, SemVer.parse("41.2.3")))
                .isEqualTo(SemVer.parse("42.0.0"));
    }

    @Test
    void determineVersionFromPartialMajor() {
        SemVer req = SemVer.parse("42");
        assertThat(SemVerUtil.determineVersion(req, SemVer.NO_VERSION)).isEqualTo(SemVer.parse("42.0.0"));
        assertThat(SemVerUtil.determineVersion(req, SemVer.parse("42.4.5"))).isEqualTo(SemVer.parse("42.5.0"));
    }

    @Test
    void determineVersionFromPartialMinor() {
        SemVer req = SemVer.parse("3.42");
        assertThat(SemVerUtil.determineVersion(req, SemVer.NO_VERSION)).isEqualTo(SemVer.parse("3.42.0"));
        assertThat(SemVerUtil.determineVersion(req, SemVer.parse("3.42.5"))).isEqualTo(SemVer.parse("3.42.6"));
    }

    @Test
    void streamAllResolutions() {
        assertThat(SemVerUtil.streamAllResolutions(SemVer.NO_VERSION)).containsExactly(SemVer.NO_VERSION);

        assertThat(SemVerUtil.streamAllResolutions(new SemVer(3, null, null, null)))
                .containsExactly(new SemVer(3, null, null, null), SemVer.NO_VERSION);

        assertThat(SemVerUtil.streamAllResolutions(new SemVer(3, 42, null, null)))
                .containsExactly(new SemVer(3, 42, null, null), new SemVer(3, null, null, null), SemVer.NO_VERSION);

        assertThat(SemVerUtil.streamAllResolutions(new SemVer(3, 42, 6, null)))
                .containsExactly(
                        new SemVer(3, 42, 6, null),
                        new SemVer(3, 42, null, null),
                        new SemVer(3, null, null, null),
                        SemVer.NO_VERSION);

        assertThat(SemVerUtil.streamAllResolutions(new SemVer(3, 42, 6, "SNAPSHOT")))
                .containsExactly(new SemVer(3, 42, 6, "SNAPSHOT"));
    }
}
