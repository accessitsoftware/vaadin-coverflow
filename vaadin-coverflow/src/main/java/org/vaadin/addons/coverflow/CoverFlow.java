package org.vaadin.addons.coverflow;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import org.vaadin.addons.coverflow.client.CoverFlowServerRpc;
import org.vaadin.addons.coverflow.client.CoverFlowState;
import org.vaadin.addons.coverflow.client.CoverflowStyle;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * The Coverflow widget uses the jQuery Addon flipster <a
 * href="https://github.com/drien/jquery-flipster">Flipster</a> to enable a
 * coverflow style image gallery.<br>
 * <br>
 * Every call to one of the CoverFlow-Setter methods will cause a complete
 * reinitialization of the coverflow on the client.
 *
 * @author Christian Thiel
 *
 * jquery version: 3.1.1 (slim version wouldn't work, migration package not required)
 * flipster version: 1.1.2
 */
@StyleSheet({ "vaadin://coverflow/jquery.flipster.min.css" })
@JavaScript({ "vaadin://coverflow/jquery.min.js", "vaadin://coverflow/jquery.flipster.min.js", "CoverFlow.js" })
public class CoverFlow extends AbstractJavaScriptComponent {

	private static final long serialVersionUID = 571299685547099480L;

	private int selectedIndex = -1;

	@SuppressWarnings("serial")
	private final CoverFlowServerRpc rpc = new CoverFlowServerRpc() {

		@Override
		public void click(final String url, final boolean initialSelection) {
			final int selection = getState(false).urlList.indexOf(url);

			selectedIndex = selection;
			if (!initialSelection) {
				fireEvent(new ImageSelectionEvent(CoverFlow.this, url, selection));
			}
		}
	};

	/**
	 * Creates a new coverflow widget with the given image URLs.
	 *
	 * @param urls
	 *            image URL list
	 */
	public CoverFlow(final List<String> urls) {
		registerRpc(this.rpc);

		setUrlList(urls);
	}

	/**
	 * Sets the image URLs. Causes a reinitialization.
	 *
	 * @param urls
	 *            the URLs
	 */
	public void setUrlList(final List<String> urls) {
		getState().urlList = urls;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * Sets the max size of the displayed images in pixels (both dimensions).
	 * Causes a reinitialization.
	 *
	 * @param maxSize
	 *            the max image size in pixels
	 */
	public void setMaxImageSize(final int maxSize) {
		getState().maxSize = maxSize;
	}

	public int getMaxImageSize() {
		return getState(false).maxSize;
	}

	/**
	 * Set true to enable global keyboard navigation with the arrow keys. Causes
	 * a reinitialization.
	 *
	 * @param keyboardEnable
	 *            if keyboard navigation should be enabled
	 */
	public void setKeyboardEnable(final boolean keyboardEnable) {
		getState().enableKeyboard = keyboardEnable;
	}

	public boolean isKeyboardEnabled() {
		return getState(false).enableKeyboard;
	}

	/**
	 * Set true to enable mousewheel navigation. Causes a reinitialization.
	 *
	 * @param mousewheelEnable
	 *            if mousewheel navigation should be enabled
	 */
	public void setMousewheelEnable(final boolean mousewheelEnable) {
		getState().enableMousewheel = mousewheelEnable;
	}

	public boolean isMousewheelEnabled() {
		return getState(false).enableMousewheel;
	}

	/**
	 * Set true to enable loop functionality, i.e. moving forward from the last element jumps to the first and vice versa.
	 * Causes a reinitialization.
	 *
	 * @param loopEnable
	 *            if loop functionality should be enabled
	 */
	public void setLoopEnable(final boolean loopEnable) {
		getState().enableLoop = loopEnable;
	}

	public boolean isLoopEnabled() {
		return getState(false).enableLoop;
	}

	/**
	 * Set true to enable navigation buttons. Causes a reinitialization.
	 *
	 * @param navigationButtonsEnable
	 *            if navigationButtonsEnable should be enabled
	 */
	public void setNavigationButtonsEnable(final boolean navigationButtonsEnable) {
		getState().enableNavigationButtons = navigationButtonsEnable;
	}

	public boolean isNavigationButtonsEnabled() {
		return getState(false).enableNavigationButtons;
	}

	/**
	 * Set milliseconds greater than 0 to activate autoplay feature.
	 *
	 * @param milliseconds null or lesser than 1 to switch autoplay off, otherwise millisecons greater than 1000 to define speed
	 */
	public void setAutoplay(final Integer milliseconds) {
		if (milliseconds == null || milliseconds <= 0)
			getState().autoplay_milliseconds = -1; // autoplay = off
		else if (milliseconds < 1000)
			getState().autoplay_milliseconds = 1000;
		else
			getState().autoplay_milliseconds = milliseconds;

		if (isAutoplay())
			setLoopEnable(true);
	}

	public boolean isAutoplay() {
		return getState().autoplay_milliseconds > 0;
	}

	/**
	 * Sets the style of the coverflow. Causes a reinitialization.
	 *
	 * @param style
	 *            the style
	 */
	public void setCoverflowStyle(final CoverflowStyle style) {
		getState().style = style;
	}

	public CoverflowStyle getCoverflowStyle() {
		return getState(false).style;
	}

	/**
	 * Sets the start position of the coverflow. Every value lower than 0 sets
	 * the start position to the center value.
	 *
	 * @param startIdx
	 *            start value
	 */
	public void setStartElement(final int startIdx) {
		getState().start = startIdx;
	}

	public int getStartElement() {
		return getState(false).start;
	}

	@Override
	protected CoverFlowState getState() {
		return (CoverFlowState) super.getState();
	}

	@Override
	protected CoverFlowState getState(final boolean markAsDirty) {
		return (CoverFlowState) super.getState(markAsDirty);
	}

	public static class ImageSelectionEvent extends Component.Event {

		private static final long serialVersionUID = -4717161002326588670L;
		private final String url;
		private final int idx;

		public ImageSelectionEvent(final Component source, final String url, final int idx) {
			super(source);
			this.url = url;
			this.idx = idx;
		}

		/**
		 * Returns the URL of the now selected image.
		 *
		 * @return the url of the selected image
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * Returns the index of the selected image in the given URL list.
		 *
		 * @return the index of the selected image
		 */
		public int getSelectedIndex() {
			return idx;
		}

	};

	public interface ImageSelectionListener extends Serializable {
		public static final Method IMAGE_SELECTION_METHOD = ReflectTools
				.findMethod(ImageSelectionListener.class, "onImageSelection", ImageSelectionEvent.class);

		public void onImageSelection(final ImageSelectionEvent event);
	}

	/**
	 * Adds an image selected listener. This listener will be called when an
	 * imageSelectionEvent occurs.
	 *
	 * @param listener
	 *            the listener to add
	 */
	public void addImageSelectionListener(final ImageSelectionListener listener) {
		addListener(ImageSelectionEvent.class, listener, ImageSelectionListener.IMAGE_SELECTION_METHOD);
	}

	/**
	 * Removes an image selectedlistener
	 *
	 * @param listener
	 *            the listener to remove
	 */
	public void removeImageSelectionListener(final ImageSelectionListener listener) {
		removeListener(ImageSelectionEvent.class, listener, ImageSelectionListener.IMAGE_SELECTION_METHOD);
	}

}
