/*
 * Copyright 2017 DV Bern AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * limitations under the License.
 */

package ch.dvbern.oss.lib.iso20022.camt054.v00104;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import ch.dvbern.oss.lib.iso20022.TestUtil;
import ch.dvbern.oss.lib.iso20022.camt.CamtTypeVersion;
import ch.dvbern.oss.lib.iso20022.camt.dtos.Account;
import ch.dvbern.oss.lib.iso20022.camt.dtos.Booking;
import ch.dvbern.oss.lib.iso20022.camt.dtos.DocumentDTO;
import ch.dvbern.oss.lib.iso20022.camt.dtos.IsrTransaction;
import ch.dvbern.oss.lib.iso20022.camt.dtos.MessageIdentifier;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the Camt054V00104Service with the file camt_054_Beispiel_ZA1_ESR_ZE.xml from SIX Interbank Clearing (see
 * www.six-interbank-clearing.com/dam/downloads/de/standardization/iso/swiss-recommendations/swiss-usage-guide
 * -examples.zip)
 */
public class Camt054V00104ServiceEsrZa1Test {

	private static final String PATH = "ch/dvbern/oss/lib/iso20022/camt054/v00104/camt_054_Beispiel_ZA1_ESR_ZE.xml";

	private final Camt054V00104Service service = new Camt054V00104Service();

	private final LocalDateTime creDtTm = LocalDateTime.of(2015, 1, 15, 9, 30, 47);
	private final DocumentDTO actual = service.getDocumentWithBookedEsrPaymentsFromXml(TestUtil.readXml(PATH));

	@Test
	public void testDocument() {
		assertEquals(CamtTypeVersion.CAMT054V00104, actual.getCamtTypeVersion());
	}

	@Test
	public void testMessageIdentification() {
		MessageIdentifier messageIdentifier = actual.getMessageIdentifier();
		assertEquals("MSGID-C053.01.00.10-110725163809-01", messageIdentifier.getMessageIdentification());
		assertEquals(creDtTm, messageIdentifier.getCreationDateTime());
		assertEquals("1", messageIdentifier.getPageNumber());
		assertTrue(messageIdentifier.isLastPage());
	}

	@Test
	public void testAccounts() {
		List<Account> accounts = actual.getAccounts();

		assertEquals(1, accounts.size());

		Account account = accounts.get(0);
		assertEquals("C054.01.00.10-110725163809-01", account.getIdentification());
		assertEquals(creDtTm, account.getCreationDateTime());
		assertEquals("CH160077401231234567", account.getAccountIdentificationIBAN());
	}

	@Test
	public void testBooking() {
		assertEquals(1, actual.getAccounts().get(0).getBookings().size());
		Booking booking = actual.getAccounts().get(0).getBookings().get(0);

		assertEquals("010391391", booking.getIsrCustomerNumber());
		assertEquals(LocalDate.of(2015, 1, 7).atStartOfDay(), booking.getBookingDate());
		assertEquals(LocalDate.of(2015, 1, 7).atStartOfDay(), booking.getValueDate());
	}

	@Test
	public void testIsrTransactions() {
		List<IsrTransaction> transactions = actual.getAccounts().get(0).getBookings().get(0).getTransactions();
		assertEquals(1, transactions.size());
		// TODO test details
	}
}
