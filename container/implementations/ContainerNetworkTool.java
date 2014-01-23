package appeng.container.implementations;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import appeng.container.AEBaseContainer;
import appeng.container.slot.SlotRestrictedInput;
import appeng.container.slot.SlotRestrictedInput.PlaceableItemType;
import appeng.helpers.INetworkTool;
import appeng.util.Platform;

public class ContainerNetworkTool extends AEBaseContainer
{

	INetworkTool toolInv;

	public ContainerNetworkTool(InventoryPlayer ip, INetworkTool te) {
		super( ip, null, null );
		toolInv = te;

		lockPlayerInventorySlot( ip.currentItem );

		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				addSlotToContainer( (new SlotRestrictedInput( PlaceableItemType.UPGRADES, te, y * 3 + x, 80 - 18 + x * 18, 37 - 18 + y * 18 )) );

		bindPlayerInventory( ip, 0, 166 - /* height of playerinventory */82 );
	}

	@Override
	public void detectAndSendChanges()
	{
		ItemStack currentItem = getPlayerInv().getCurrentItem();

		if ( currentItem != toolInv.getItemStack() )
		{
			if ( currentItem != null )
			{
				if ( Platform.isSameItem( toolInv.getItemStack(), currentItem ) )
					getPlayerInv().setInventorySlotContents( getPlayerInv().currentItem, toolInv.getItemStack() );
				else
					getPlayerInv().player.closeScreen();
			}
			else
				getPlayerInv().player.closeScreen();
		}

		super.detectAndSendChanges();
	}
}