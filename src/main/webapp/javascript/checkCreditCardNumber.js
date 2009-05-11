/*
 * ConcourseConnect
 * Copyright 2009 Concursive Corporation
 * http://www.concursive.com
 *
 * This file is part of ConcourseConnect, an open source social business
 * software and community platform.
 *
 * Concursive ConcourseConnect is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, version 3 of the License.
 *
 * Under the terms of the GNU Affero General Public License you must release the
 * complete source code for any application that uses any part of ConcourseConnect
 * (system header files and libraries used by the operating system are excluded).
 * These terms must be included in any work that has ConcourseConnect components.
 * If you are developing and distributing open source applications under the
 * GNU Affero General Public License, then you are free to use ConcourseConnect
 * under the GNU Affero General Public License. Ê
 *
 * If you are deploying a web site in which users interact with any portion of
 * ConcourseConnect over a network, the complete source code changes must be made
 * available.  For example, include a link to the source archive directly from
 * your web site.
 *
 * For OEMs, ISVs, SIs and VARs who distribute ConcourseConnect with their
 * products, and do not license and distribute their source code under the GNU
 * Affero General Public License, Concursive provides a flexible commercial
 * license.
 *
 * To anyone in doubt, we recommend the commercial license. Our commercial license
 * is competitively priced and will eliminate any confusion about how
 * ConcourseConnect can be used and distributed.
 *
 * ConcourseConnect is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ConcourseConnect.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Attribution Notice: ConcourseConnect is an Original Work of software created
 * by Concursive Corporation
 */

function checkCardNumWithMod10(cardNum) {
	var i;
	var cc = new Array(16);
	var checksum = 0;
	var validcc;

	// assign each digit of the card number to a space in the array	
	for (i = 0; i < cardNum.length; i++) {
		cc[i] = Math.floor(cardNum.substring(i, i+1));
	}

	// walk through every other digit doing our magic
	// if the card number is sixteen digits then start at the
	// first digit (position 0), otherwise start from the
	// second (position 1)
	for (i = (cardNum.length % 2); i < cardNum.length; i+=2) {
		var a = cc[i] * 2;
		if (a >= 10) {
			var aStr = a.toString();
			var b = aStr.substring(0,1);
			var c = aStr.substring(1,2);
			cc[i] = Math.floor(b) + Math.floor(c);
		} else {
			cc[i] = a;
		}
	}

	// add up all of the digits in the array
	for (i = 0; i < cardNum.length; i++) {
		checksum += Math.floor(cc[i]);
	}

	// if the checksum is evenly divisble by 10
	// then this is a valid card number
	validcc = ((checksum % 10) == 0);
  return validcc;
}

function cleanCardNum(cardNum) {
	var i;
	var ch;
	var newCard = "";

	// walk through the string character by character to build
	// a new string with numbers only
	i = 0;
	while (i < cardNum.length) {
		// get the current character
		ch = cardNum.substring(i, i+1);
		if ((ch >= "0") && (ch <= "9")) {
			// if the current character is a digit then add it
			// to the numbers-only string we're building
			newCard += ch;
		} else {
			// not a digit, so check if its a dash or a space
			if ((ch != " ") && (ch != "-")) {
				// not a dash or a space so fail
				alert("The card number contains invalid characters.");
				return "";
			}
		}
		i++;
	}

	// we got here if we didn't fail, so return what we built
	return newCard;
}

function checkCard(cardType, cardNum) {
	var validCard;
	var cardLength;
	var cardLengthOK;
	var cardStart;
	var cardStartOK;
	
	// check if the card type is valid
	if ((cardType != "V") && (cardType != "M") && (cardType != "A") && (cardType != "D")) {
		alert("Please select a card type.");
		return false;
	}

	// clean up any spaces or dashes in the card number
	validCard = cleanCardNum(cardNum);
	if (validCard != "") {
		// check the first digit to see if it matches the card type
		cardStart = validCard.substring(0,1);
		cardStartOK = ( ((cardType == "V") && (cardStart == "4")) ||
				((cardType == "M") && (cardStart == "5")) ||
				((cardType == "A") && (cardStart == "3")) ||
				((cardType == "D") && (cardStart == "6")) );
		if (!(cardStartOK)) {
			// card number's first digit doesn't match card type
			alert("Please make sure the card number you've entered matched the card type you selected.");
			return false;
		}

		// the card number is good now, so check to make sure
		// it's a the right length
		cardLength = validCard.length;		
		cardLengthOK = ( ((cardType == "V") && ((cardLength == 13) || (cardLength == 16))) ||
				 ((cardType == "M") && (cardLength == 16)) ||
				 ((cardType == "A") && (cardLength == 15)) ||
				 ((cardType == "D") && (cardLength == 16)) );
		if (!(cardLengthOK)) {
			// not the right length
			alert("Please make sure you've entered all of the digits on your card.");
			return false;
		}

		// card number seems OK so do the Mod10
		if (checkCardNumWithMod10(validCard)) {
			return true;
		} else {
			alert("Please make sure you've entered your card number correctly.");
			return false;
		}
	} else {
		return false;
	}
}
