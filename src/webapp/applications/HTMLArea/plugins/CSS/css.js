// Simple CSS (className) plugin for the editor
// Sponsored by http://www.miro.com.au
// Implementation by Mihai Bazon, http://dynarch.com/mishoo.
//
// (c) dynarch.com 2003
// Distributed under the same terms as HTMLArea itself.
// This notice MUST stay intact for use (see license.txt).
//
// $Id: css.js,v 1.4 2005/02/28 16:14:07 mattias Exp $
function CSS(editor, params) {
	this.editor = editor;
	var cfg = editor.config;
	var toolbar = cfg.toolbar;
	var self = this;
	var i18n = CSS.I18N;
	var plugin_config = params[0];
	var combos = plugin_config.combos;

	var first = true;
	for (var i = combos.length; --i >= 0;) {
		var combo = combos[i];
		var id = "CSS-class" + i;
		var css_class = {
			id         : id,
			options    : combo.options,
			action     : function(editor) { self.onSelect(editor, this, combo.context, combo.updatecontextclass); },
			refresh    : function(editor) { self.updateValue(editor, this); },
			context    : combo.context
		};
		cfg.registerDropdown(css_class);

		// prepend to the toolbar
		//alert(toolbar.length)
		index = 0;
		if(toolbar.length > 1)
		{
			index = toolbar.length - 1;
		}
		//alert(index);
		toolbar[index].splice(0, 0, first ? "separator" : "space");
		toolbar[index].splice(0, 0, id);
		if (combo.label)
			toolbar[index].splice(0, 0, "T[" + combo.label + "]");
		first = false;
	}
};

CSS._pluginInfo = {
	name          : "CSS",
	version       : "1.0",
	developer     : "Mihai Bazon",
	developer_url : "http://dynarch.com/mishoo/",
	c_owner       : "Mihai Bazon",
	sponsor       : "Miro International",
	sponsor_url   : "http://www.miro.com.au",
	license       : "htmlArea"
};

CSS.prototype.onSelect = function(editor, obj, context, updatecontextclass) {
	var tbobj = editor._toolbarObjects[obj.id];
	var index = tbobj.element.selectedIndex;
	var className = tbobj.element.value;

	// retrieve parent element of the selection
	var parent = editor.getParentElement();
	var surround = true;

	var is_span = (parent && parent.tagName.toLowerCase() == "span");
	var update_parent = (context && updatecontextclass && parent && parent.tagName.toLowerCase() == context);
	
	if (update_parent) {
		parent.className = className;
		editor.updateToolbar();
		return;
	}

	if (is_span && index == 0 && !/\S/.test(parent.style.cssText)) {
		while (parent.firstChild) {
			parent.parentNode.insertBefore(parent.firstChild, parent);
		}
		parent.parentNode.removeChild(parent);
		editor.updateToolbar();
		return;
	}
	
	if (is_span) {
		// maybe we could simply change the class of the parent node?
		if (parent.childNodes.length == 1) {
			parent.className = className;
			surround = false;
			// in this case we should handle the toolbar updation
			// ourselves.
			editor.updateToolbar();
		}
	}

	// Other possibilities could be checked but require a lot of code.  We
	// can't afford to do that now.
	if (surround) {
		// shit happens ;-) most of the time.  this method works, but
		// it's dangerous when selection spans multiple block-level
		// elements.
		editor.surroundHTML("<span class='" + className + "'>", "</span>");
	}
};

CSS.prototype.updateValue = function(editor, obj) {
	var select = editor._toolbarObjects[obj.id].element;
	var parent = editor.getParentElement();
	if (parent && typeof parent.className != "undefined" && /\S/.test(parent.className)) {
		var options = select.options;
		var value = parent.className;
		for (var i = options.length; --i >= 0;) {
			var option = options[i];
			if (value == option.value) {
				select.selectedIndex = i;
				return;
			}
		}
	}
	select.selectedIndex = 0;
};
