package buildermaster.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import buildermaster.Activator;
import buildermaster.BuilderMaster;
import buildermaster.BuilderMasterPlugin;
import buildermaster.ClientClassLoader;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
	    TreeSelection objetoSelecionado = (TreeSelection) HandlerUtil.getCurrentSelection(event);
	    
	    ICompilationUnit unit = (ICompilationUnit) objetoSelecionado.getFirstElement();
		try {
			new BuilderMasterPlugin().gerarCodigoClasse(unit);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Shell shell = HandlerUtil.getActiveShell(event);
//	    ISelection sel = HandlerUtil.getActiveMenuSelection(event);
//	    IStructuredSelection selection = (IStructuredSelection) sel;

	    MessageDialog.openInformation(shell, "Info",
          "Código do Builder na Área de Transferência");

		return null;
	}
}
