/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ============LICENSE_END============================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */

package org.onap.portalsdk.fw.test;

import java.util.Date;

public abstract class AbstractModelTest {
	
	// Values for properties
	final long time = new Date().getTime();
	final boolean b1 = true;
	final boolean b2 = false;
	final Byte[] by1 = { 0, 1, 2, 3 };
	final Date d1 = new Date(time + 1 * 24 * 60 * 60 * 1000);
	final Date d2 = new Date(time + 2 * 24 * 60 * 60 * 1000);
	final Date d3 = new Date(time + 3 * 24 * 60 * 60 * 1000);
	final Date d4 = new Date(time + 4 * 24 * 60 * 60 * 1000);
	final Date d5 = new Date(time + 5 * 24 * 60 * 60 * 1000);
	final Integer i1 = 1;
	final Integer i2 = 2;
	final Integer i3 = 3;
	final Integer i4 = 4;
	final Integer i5 = 5;
	final Long l1 = 1L;
	final Long l2 = 2L;
	final Long l3 = 3L;
	final Long l4 = 4L;
	final String s1 = "string1";
	final String s2 = "string2";
	final String s3 = "string3";
	final String s4 = "string4";
	final String s5 = "string5";
	final String s6 = "string6";
	final String s7 = "string7";
	final String s8 = "string8";
	final String s9 = "string9";
	final String s10 = "string10";

}
