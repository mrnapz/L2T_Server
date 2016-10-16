/*
 * $Header$
 *
 * $Author$
 * $Date$
 * $Revision$
 * $Log$
 * Revision 1.1.2.1  2005/04/08 08:03:40  luisantonioa
 * *** empty log message ***
 *
 * Revision 1.1  4/04/2005 17:15:07  luisantonioa
 * Created New Class
 *
 *
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

package l2server.gameserver.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class ...
 *
 * @version $Revision$ $Date$
 */

public class DesireTable
{
	public static final DesireType[] DEFAULT_DESIRES =
			{DesireType.FEAR, DesireType.DISLIKE, DesireType.HATE, DesireType.DAMAGE};

	public enum DesireType
	{
		FEAR, DISLIKE, HATE, DAMAGE
	}

	static class DesireValue
	{
		private float value;

		DesireValue()
		{
			this(0f);
		}

		DesireValue(Float pValue)
		{
			this.value = pValue;
		}

		public void addValue(float pValue)
		{
			this.value += pValue;
		}

		public float getValue()
		{
			return this.value;
		}
	}

	class Desires
	{
		private Map<DesireType, DesireValue> desireTable;

		public Desires(DesireType... desireList)
		{
			this.desireTable = new HashMap<>();

			for (DesireType desire : desireList)
			{
				this.desireTable.put(desire, new DesireValue());
			}
		}

		public DesireValue getDesireValue(DesireType type)
		{
			return this.desireTable.get(type);
		}

		public void addValue(DesireType type, float value)
		{
			DesireValue temp = getDesireValue(type);
			if (temp != null)
			{
				temp.addValue(value);
			}
		}

		public void createDesire(DesireType type)
		{
			this.desireTable.put(type, new DesireValue());
		}

		public void deleteDesire(DesireType type)
		{
			this.desireTable.remove(type);
		}
	}

	private Map<L2Object, Desires> objectDesireTable;
	private Desires generalDesires;
	private DesireType[] desireTypes;

	public DesireTable(DesireType... desireList)
	{
		this.desireTypes = desireList;
		this.objectDesireTable = new HashMap<>();
		this.generalDesires = new Desires(this.desireTypes);
	}

	public float getDesireValue(DesireType type)
	{
		return this.generalDesires.getDesireValue(type).getValue();
	}

	public float getDesireValue(L2Object object, DesireType type)
	{
		Desires desireList = this.objectDesireTable.get(object);
		if (desireList == null)
		{
			return 0f;
		}
		return desireList.getDesireValue(type).getValue();
	}

	public void addDesireValue(DesireType type, float value)
	{
		this.generalDesires.addValue(type, value);
	}

	public void addDesireValue(L2Object object, DesireType type, float value)
	{
		Desires desireList = this.objectDesireTable.get(object);
		if (desireList != null)
		{
			desireList.addValue(type, value);
		}
	}

	public void createDesire(DesireType type)
	{
		this.generalDesires.createDesire(type);
	}

	public void deleteDesire(DesireType type)
	{
		this.generalDesires.deleteDesire(type);
	}

	public void createDesire(L2Object object, DesireType type)
	{
		Desires desireList = this.objectDesireTable.get(object);
		if (desireList != null)
		{
			desireList.createDesire(type);
		}
	}

	public void deleteDesire(L2Object object, DesireType type)
	{
		Desires desireList = this.objectDesireTable.get(object);
		if (desireList != null)
		{
			desireList.deleteDesire(type);
		}
	}

	public void addKnownObject(L2Object object)
	{
		if (object != null)
		{
			addKnownObject(object, DesireType.DISLIKE, DesireType.FEAR, DesireType.DAMAGE, DesireType.HATE);
		}
	}

	public void addKnownObject(L2Object object, DesireType... desireList)
	{
		if (object != null)
		{
			this.objectDesireTable.put(object, new Desires(desireList));
		}
	}
}
