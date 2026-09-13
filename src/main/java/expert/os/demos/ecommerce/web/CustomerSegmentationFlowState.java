package expert.os.demos.ecommerce.web;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CustomerSegmentationFlowState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<ThresholdInput> thresholds = List.of();
    private List<TierPreview> preview = List.of();
    private long previewTotal;

    public List<ThresholdInput> getThresholds() {
        return thresholds;
    }

    void setThresholds(List<ThresholdInput> thresholds) {
        this.thresholds = List.copyOf(thresholds);
    }

    public List<TierPreview> getPreview() {
        return preview;
    }

    public long getPreviewTotal() {
        return previewTotal;
    }

    void setPreview(List<TierPreview> preview, long previewTotal) {
        this.preview = List.copyOf(preview);
        this.previewTotal = previewTotal;
    }
}
