There is a lot of folders here now. It's about to be reorganised in a simpler structure. Proposed structure:

src                    sources for the core and the sdk. xjavadoc should be moved here too.
  plugins              sources for various plugins. these will be outsourced as soon as the core is stable
    xxx
    yyy
lib                    our own maven repository for jars not yet at ibiblio.
aptdocs                all handwritten docs for xdoclet.
maven-aptdoc-plugin    the maven aptdoc plugin (it was kicked out of maven's cvs because of the license)

Proposal of what should happen to the other folders:

maven-castor-plugin    will be deleted. we're not going to need it
maven-zeus-plugin      will be merged into the sdk
plugins                will be deleted and moved under src/plugins (and then outsourced)
xdoc                   will be deleted, as all docs will be based on aptdoc
xrai                   will be moved into a separate CVS module or merged with jakarta commons-attributes or nanning?