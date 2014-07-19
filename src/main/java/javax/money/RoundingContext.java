/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
package javax.money;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;

/**
 * This class models the spec/configuration for a rounding, modelled as {@link javax.money.MonetaryRounding} in a
 * platform independent way. Each RoundingContext instance hereby has a <code>roundingId</code>, which links
 * to the {@link javax.money.spi.RoundingProviderSpi} that must create the according rounding instance. The
 * <i>default</i> </i><code>roundingId</code> is <code>default</code>.<br/>
 * A RoundingContext can take up arbitrary attributes that must be documented by the
 * {@link javax.money.spi.RoundingProviderSpi} implementations.
 * <p>
 * Examples for such additional attributes are
 * {@link java.math.RoundingMode}, {@link java.math.MathContext}, additional regional information,
 * e.g. if a given rounding is targeting cash rounding.
 * <p>
 * This class is immutable, serializable, thread-safe.
 *
 * @author Anatole Tresch
 */
public final class RoundingContext extends AbstractContext implements Serializable{


    /**
     * Constructor, used from the {@link javax.money.RoundingContext.Builder}.
     *
     * @param builder the corresponding builder, not null.
     */
    private RoundingContext(Builder builder){
        super(builder);
    }

    /**
     * Get the (custom) rounding id.
     *
     * @return the rounding id, or null.
     */
    public String getRoundingId(){
        return getText("roundingId");
    }

    /**
     * Get the basic {@link javax.money.CurrencyUnit}, which is based for this rounding type.
     *
     * @return the target CurrencyUnit, or null.
     */
    public CurrencyUnit getCurrencyUnit(){
        return get(CurrencyUnit.class, (CurrencyUnit) null);
    }

    /**
     * Get the current timestamp of the context in UTC milliseconds.  If not set it tries to create an
     * UTC timestamp from #getTimestamp().
     *
     * @return the timestamp in millis, or null.
     */
    public Long getTimestampMillis(){
        Long value = getLong("timestamp", null);
        if(Objects.isNull(value)){
            TemporalAccessor acc = getTimestamp();
            if(Objects.nonNull(acc)){
                return (acc.getLong(ChronoField.INSTANT_SECONDS) * 1000L) + acc.getLong(ChronoField.MILLI_OF_SECOND);
            }
        }
        return value;
    }

    /**
     * Get the current timestamp. If not set it tries to create an Instant from #getTimestampMillis().
     *
     * @return the current timestamp, or null.
     */
    public TemporalAccessor getTimestamp(){
        TemporalAccessor acc = getAny("timestamp", TemporalAccessor.class, null);
        if(Objects.isNull(acc)){
            Long value = getLong("timestamp", null);
            if(Objects.nonNull(value)){
                acc = Instant.ofEpochMilli(value);
            }
        }
        return acc;
    }

    /**
     * Get the rounding's scale.
     *
     * @return the scale, if set, or null.
     */
    public Integer getScale(){
        return getInt("scale", null);
    }

    /**
     * Returns the {@code precision} setting. This value is always non-negative.
     *
     * @return an {@code int} which is the value of the {@code precision}
     * setting
     */
    public String getProvider(){
        return getText(PROVIDER);
    }


    /**
     * Allows to convert a instance into the corresponding {@link javax.money.CurrencyContext.Builder}, which allows
     * to change the values and create another {@link javax.money.CurrencyContext} instance.
     *
     * @return a new Builder instance, preinitialized with the values from this instance.
     */
    public RoundingContext.Builder toBuilder(){
        return new Builder(getProvider(), getRoundingId()).importContext(this);
    }

    /**
     * Builder class for creating new instances of {@link javax.money.RoundingContext} adding detailed information
     * about a {@link javax.money.MonetaryRounding} instance.
     * <p/>
     * Note this class is NOT thread-safe.
     *
     * @see MonetaryRounding#getRoundingContext() ()
     */
    public static final class Builder extends AbstractContextBuilder<Builder,RoundingContext>{

        /**
         * Creates a new builder.
         *
         * @param provider   the provider name, creating the corresponding {@link javax.money.MonetaryRounding}
         *                   containing, not null.
         *                   the final {@link javax.money.RoundingContext} created by this builder, not null.
         * @param roundingId The name of the rounding, not null.
         */
        public Builder(String provider, String roundingId){
            Objects.requireNonNull(provider);
            set("provider", provider);
            Objects.requireNonNull(roundingId);
            set("roundingId", roundingId);
        }

        /**
         * Get the basic {@link javax.money.CurrencyUnit}, which is based for this rounding type.
         *
         * @return the target CurrencyUnit, or null.
         */
        public Builder setCurrencyUnit(CurrencyUnit currencyUnit){
            Objects.requireNonNull(currencyUnit);
            return set(currencyUnit, CurrencyUnit.class);
        }

        /**
         * Sets the currency's timestamp, using UTC milliseconds.
         *
         * @param timestamp the timestamp.
         * @return the builder for chaining
         */
        public Builder setTimestamp(long timestamp){
            set("timestamp", timestamp);
            return this;
        }

        /**
         * Sets the currency's timestamp.
         *
         * @param timestamp the timestamp, not null.
         * @return the builder for chaining
         */
        public Builder setTimestamp(TemporalAccessor timestamp){
            set("timestamp", timestamp, TemporalAccessor.class);
            return this;
        }

        /**
         * Creates a new instance of {@link RoundingContext}.
         *
         * @return a new {@link RoundingContext} instance.
         */
        @Override
        public RoundingContext build(){
            return new RoundingContext(this);
        }

    }
}
