/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package l2server.gameserver.network.serverpackets;

import l2server.Config;
import l2server.gameserver.model.L2Clan;

/**
 * sample
 * 0000: cd b0 98 a0 48 1e 01 00 00 00 00 00 00 00 00 00	....H...........
 * 0010: 00 00 00 00 00									 .....
 * <p>
 * format   ddddd
 *
 * @version $Revision: 1.1.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public final class PledgeStatusChanged extends L2GameServerPacket
{
	private L2Clan clan;

	public PledgeStatusChanged(L2Clan clan)
	{
		this.clan = clan;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(Config.SERVER_ID);
		writeD(this.clan.getLeaderId());
		writeD(this.clan.getClanId());
		writeD(this.clan.getCrestId());
		writeD(this.clan.getAllyId());
		writeD(this.clan.getAllyCrestId());
		writeD(0);
		writeD(0);
	}
}
