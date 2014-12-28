/*
 * Copyright 2008-9 Compulsion
 * <pes_compulsion@yahoo.co.uk>
 * <http://www.purplehaze.eclipse.co.uk/>
 * <http://uk.geocities.com/pes_compulsion/>
 *
 * This file is part of PES Editor.
 *
 * PES Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PES Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PES Editor.  If not, see <http://www.gnu.org/licenses/>.
 */

package editor;

import editor.data.OptionFile;

public class Kits {
	static final int totalN = 67;

	static final int startAdrN = 751254;

	static final int sizeN = 456;

	static final int startAdrC = 781806;

	static final int sizeC = 648;

	static boolean logoUsed(OptionFile of, int team, int logo) {
		int a = startAdrC + 358 + (sizeC * team) + (logo * 24) + 2;
		if (team >= Clubs.TOTAL) {
			team = team - Clubs.TOTAL;
			a = startAdrN + 358 + (sizeN * team) + (logo * 24) + 2;
		}
		if (of.getData()[a] == 1) {
			return true;
		} else {
			return false;
		}
	}

	static byte getLogo(OptionFile of, int team, int logo) {
		int a = startAdrC + 358 + (sizeC * team) + (logo * 24) + 3;
		if (team >= Clubs.TOTAL) {
			team = team - Clubs.TOTAL;
			a = startAdrN + 358 + (sizeN * team) + (logo * 24) + 3;
		}
		return of.getData()[a];
	}

	static void setLogo(OptionFile of, int team, int logo, byte slot) {
		int a = startAdrC + 358 + (sizeC * team) + (logo * 24) + 3;
		if (team >= Clubs.TOTAL) {
			team = team - Clubs.TOTAL;
			a = startAdrN + 358 + (sizeN * team) + (logo * 24) + 3;
		}
		of.getData()[a] = slot;
	}

	static void setLogoUnused(OptionFile of, int team, int logo) {
		int a = startAdrC + 358 + (sizeC * team) + (logo * 24) + 2;
		if (team >= Clubs.TOTAL) {
			team = team - Clubs.TOTAL;
			a = startAdrN + 358 + (sizeN * team) + (logo * 24) + 2;
		}
		of.getData()[a] = 0;
		of.getData()[a + 1] = 88;
	}

	static void importKit(OptionFile of1, int team1, OptionFile of2, int team2) {
		// int t = team1;
		int a1 = startAdrC + (sizeC * team1);
		int a2 = startAdrC + (sizeC * team2);
		int size = sizeC;
		if (team1 >= Clubs.TOTAL) {
			team1 = team1 - Clubs.TOTAL;
			a1 = startAdrN + (sizeN * team1);
			team2 = team2 - Clubs.TOTAL;
			a2 = startAdrN + (sizeN * team2);
			size = sizeN;
		}
		System.arraycopy(of2.getData(), a2, of1.getData(), a1, size);

		/*
		 * if (!of1.isWE() && of2.isWE()) { Convert.kitModel(of1, t); }
		 */
	}

	static boolean isLic(OptionFile of,int team){
		int a = startAdrC + 78 + (sizeC * team);
		int b = startAdrC + 79 + (sizeC * team);
		//System.out.println(Clubs.getName(of, team) + " : " +of.data[a] +" " +of.data[b]);
		if(team>= Clubs.TOTAL){
			team = team - Clubs.TOTAL;
			a = startAdrN + 78 + (sizeN * team);
			b= startAdrN + 79 + (sizeN * team);
		//	System.out.println(Stats.NATION[team] + " : " +of.data[a] +" " +of.data[b]);
		}
		if(of.getData()[a] != -1 && of.getData()[b]!= -1) {
			return true;
		} 
		return false;
	}
}
