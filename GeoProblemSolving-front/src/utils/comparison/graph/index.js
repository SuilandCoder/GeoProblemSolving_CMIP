import mx from 'mxgraph';

const mxgraph = mx({
  mxBasePath: '/GeoProblemSolving/static/mxgraph',
});

// decode bug https://github.com/jgraph/mxgraph/issues/49
window.mxGraph = mxgraph.mxGraph;
window.mxGraphModel = mxgraph.mxGraphModel;
window.mxEditor = mxgraph.mxEditor;
window.mxGeometry = mxgraph.mxGeometry;
window.mxDefaultKeyHandler = mxgraph.mxDefaultKeyHandler;
window.mxDefaultPopupMenu = mxgraph.mxDefaultPopupMenu;
window.mxStylesheet = mxgraph.mxStylesheet;
window.mxDefaultToolbar = mxgraph.mxDefaultToolbar;
window.mxStencilRegistry = mxgraph.mxStencilRegistry;
window.mxUtils=mxgraph.mxUtils;
window.mxStencil = mxgraph.mxStencil;
// Parses the given stencil set


// let shape = new mxgraph.mxImageShape(new mxgraph.mxRectangle(100,100,120,80),'/static/Images/paralleogram.svg');

// mxStencilRegistry.addStencil("Parallelepiped", new mxStencil(shape));



export default mxgraph;
