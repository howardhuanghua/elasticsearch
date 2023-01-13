package org.elasticsearch.compute.aggregation;

import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.Optional;
import org.elasticsearch.compute.data.AggregatorStateVector;
import org.elasticsearch.compute.data.Block;
import org.elasticsearch.compute.data.Page;
import org.elasticsearch.compute.data.Vector;

/**
 * {@link AggregatorFunction} implementation for {@link MedianAbsoluteDeviationDoubleAggregator}.
 * This class is generated. Do not edit it.
 */
public final class MedianAbsoluteDeviationDoubleAggregatorFunction implements AggregatorFunction {
  private final MedianAbsoluteDeviationStates.UngroupedState state;

  private final int channel;

  public MedianAbsoluteDeviationDoubleAggregatorFunction(int channel,
      MedianAbsoluteDeviationStates.UngroupedState state) {
    this.channel = channel;
    this.state = state;
  }

  public static MedianAbsoluteDeviationDoubleAggregatorFunction create(int channel) {
    return new MedianAbsoluteDeviationDoubleAggregatorFunction(channel, MedianAbsoluteDeviationDoubleAggregator.initSingle());
  }

  @Override
  public void addRawInput(Page page) {
    assert channel >= 0;
    Block block = page.getBlock(channel);
    Optional<Vector> vector = block.asVector();
    if (vector.isPresent()) {
      addRawVector(vector.get());
    } else {
      addRawBlock(block);
    }
  }

  private void addRawVector(Vector vector) {
    for (int i = 0; i < vector.getPositionCount(); i++) {
      MedianAbsoluteDeviationDoubleAggregator.combine(state, vector.getDouble(i));
    }
  }

  private void addRawBlock(Block block) {
    for (int i = 0; i < block.getTotalValueCount(); i++) {
      if (block.isNull(i) == false) {
        MedianAbsoluteDeviationDoubleAggregator.combine(state, block.getDouble(i));
      }
    }
  }

  @Override
  public void addIntermediateInput(Block block) {
    assert channel == -1;
    Optional<Vector> vector = block.asVector();
    if (vector.isEmpty() || vector.get() instanceof AggregatorStateVector == false) {
      throw new RuntimeException("expected AggregatorStateBlock, got:" + block);
    }
    @SuppressWarnings("unchecked") AggregatorStateVector<MedianAbsoluteDeviationStates.UngroupedState> blobVector = (AggregatorStateVector<MedianAbsoluteDeviationStates.UngroupedState>) vector.get();
    MedianAbsoluteDeviationStates.UngroupedState tmpState = new MedianAbsoluteDeviationStates.UngroupedState();
    for (int i = 0; i < block.getPositionCount(); i++) {
      blobVector.get(i, tmpState);
      MedianAbsoluteDeviationDoubleAggregator.combineStates(state, tmpState);
    }
  }

  @Override
  public Block evaluateIntermediate() {
    AggregatorStateVector.Builder<AggregatorStateVector<MedianAbsoluteDeviationStates.UngroupedState>, MedianAbsoluteDeviationStates.UngroupedState> builder =
        AggregatorStateVector.builderOfAggregatorState(MedianAbsoluteDeviationStates.UngroupedState.class, state.getEstimatedSize());
    builder.add(state);
    return builder.build().asBlock();
  }

  @Override
  public Block evaluateFinal() {
    return MedianAbsoluteDeviationDoubleAggregator.evaluateFinal(state);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName()).append("[");
    sb.append("channel=").append(channel);
    sb.append("]");
    return sb.toString();
  }
}
