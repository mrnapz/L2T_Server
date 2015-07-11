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
package l2tserver.gameserver.network.serverpackets;

import l2tserver.Config;

/**
 * @author Erlando
 *
 */
public class ExLoginVitalityEffectInfo extends L2GameServerPacket
{
	private static final String _S__FE_11E_EXLOGINVITALITYEFFECTINFO = "[S] FE:11E ExLoginVitalityEffectInfo";
	
	private float _expBonus;
	private int _vitalityItemsUsed;
	
	public ExLoginVitalityEffectInfo(int vitPoints, int vitalityItemsUsed)
	{
		if (vitPoints > 0)
			_expBonus = Config.VITALITY_MULTIPLIER * 100;
		else
			_expBonus = 0;
		
		_vitalityItemsUsed = vitalityItemsUsed;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x119);
		writeD((int)_expBonus);
		writeD(_vitalityItemsUsed);
	}

	@Override
	public String getType()
	{
		return _S__FE_11E_EXLOGINVITALITYEFFECTINFO;
	}
}