package jmr.home.apps;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import jmr.home.model.Atom;

public class AtomTree {

	private final Tree tree;
	private final TreeColumn tcName;
	private final TreeColumn tcPlanet;
	private final TreeColumn tcStatus;

	private final Display display;

	public final Map<String,AtomData> mapAtoms = new HashMap<>();
//	public final List<Atom> listAtoms = new LinkedList<Atom>();
	
	
	
	public static class AtomData {
		
//		Map<String,String> map = new HashMap<>();
		final Atom atom;
		
		final TreeItem item;
		
		final String strStatus;
		
		public AtomData(	final TreeItem item,
							final Atom atom,
							final String strStatus ) {
			this.item = item;
			this.atom = atom;
			this.strStatus = strStatus;
		}
		
//		public void setAtom( final Atom atom ) {
//			this.atom = atom;
//		}
		
//		public void setData( final Map<String,String> ) {
//			this.planet = planet;
//		}
	}
	
	
	public AtomTree( final Composite comp ) {
		this.tree = new Tree( comp, 
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL 
				| SWT.SINGLE | SWT.FULL_SELECTION );
		this.display = comp.getDisplay();
		
		tree.setHeaderVisible( true );
		
		tcName = new TreeColumn( tree, SWT.LEFT );
		tcName.setText( "Atom" );
		tcName.setWidth( 100 );
		
		tcPlanet = new TreeColumn( tree, SWT.LEFT );
		tcPlanet.setText( "Variable" );
		tcPlanet.setWidth( 120 );
		
		tcStatus = new TreeColumn( tree, SWT.LEFT );
		tcStatus.setText( "Value" );
		tcStatus.setWidth( 200 );
	}
	
	public Tree getTree() {
		return this.tree;
	}

	
	public void setAtom(	final Atom atom,
							final String strStatus ) {
		if ( null==atom ) return;

		final String strAtomID = atom.getName();
		
//		final ConnectorData[] cd = new ConnectorData[]{ null };
		display.asyncExec( new Runnable() {

			@Override
			public void run() {
				
				tree.setRedraw( false );
				
				final TreeItem item;
				final AtomData cdExisting = mapAtoms.get( strAtomID );
				if ( null!=cdExisting ) {
					item = cdExisting.item;
					if ( null!=strStatus ) {
						item.setText( 2, strStatus );
					} else {
						item.dispose();
						mapAtoms.remove( strAtomID );
					}
				} else {
					if ( null!=strStatus ) {
						item = new TreeItem( tree, SWT.NONE );
						
						final String[] row = new String[] { 
//								strAtomID + " " + strStatus,
								strAtomID,
								"", "" };
						item.setText( row );
						
						final AtomData 
								cdNew = new AtomData( item, atom, strStatus );
						mapAtoms.put( strAtomID, cdNew );

					} else {
						item = null;
						mapAtoms.remove( strAtomID );
					}
				}
				
				if ( null!=item && !item.isDisposed() ) {
					updateItem( item, atom );
				}
				
				tree.setRedraw( true );
			}
			
		});
//		mapConnectors.put( port, cd[0] );
//		return cd[0];
	}
	protected void updateItem(	final TreeItem item, 
								final Atom atom ) {
		if ( null==item ) return;
		if ( null==atom ) return;
		
//		for ( final Entry<String, String> entry : atom.entrySet() ) {
		for ( final String strName : atom.getOrderedKeys() ) {
//			final String strName = entry.getKey();
//			final String strValue = entry.getValue();
			String strValue = atom.get( strName );
			strValue.replaceAll( "\\n", Text.DELIMITER );

			TreeItem tiItem = null;

			final boolean bMultiField = strValue.contains( Atom.VAL_FIELD_DELIM );
			final String[] arrElements = strValue.split( Atom.VAL_FIELD_DELIM );
			
			final String strItemDisplay;
			if ( bMultiField ) {
				strItemDisplay = "(" + arrElements.length + " items)";
			} else {
				strItemDisplay = strValue;
			}

			boolean bFound = false;
			for ( final TreeItem tiSub : item.getItems() ) {
				if ( tiSub.getText( 1 ).equals( strName ) ) {
					tiItem = tiSub;
					tiSub.setText( 2, strItemDisplay );
					tiSub.setData( atom );
					bFound = true;
				}
			}
			if ( !bFound ) {
				final String[] values = new String[] {
												"",
												strName,
												strItemDisplay };
				final TreeItem tiNew = new TreeItem( item, SWT.NONE );
				tiNew.setText( values );
				tiNew.setData( atom );
				tiItem = tiNew;
			}
			
			if ( null!=tiItem && bMultiField ) {
				tiItem.removeAll();
				for ( final String strElement : arrElements ) {
					final TreeItem tiElement = new TreeItem( tiItem, SWT.NONE );
					tiElement.setText( new String[] { "","", strElement } );
				}
			}
		}
		for ( final TreeItem tiSub : item.getItems() ) {
			final Object objAtom = tiSub.getData();
			if ( atom != objAtom ) {
				tiSub.dispose();
			}
		}
		if ( Atom.Type.EVENT.equals( atom.type ) ) {
			item.setExpanded( true );
		}
	}

	protected void _updateItem(	final TreeItem item, 
								final Atom atom ) {
		item.removeAll();
		for ( final Entry<String, String> entry : atom.entrySet() ) {
			final String strName = entry.getKey();
			final String strValue = entry.getValue();
			final String[] values = new String[] {
					"",
					strName,
					strValue };
			final TreeItem ti = new TreeItem( item, SWT.NONE );
			ti.setText( values );
		}
	}
	
//	public void setLineStatus(	final SerialConnector.ComPort port,
//								final String strStatus ) {
//		if ( null==port ) return;
//		
//		final ConnectorData cd = mapConnectors.get( port );
//		if ( null==cd ) return;
//		
//	}
	
	
	
}
