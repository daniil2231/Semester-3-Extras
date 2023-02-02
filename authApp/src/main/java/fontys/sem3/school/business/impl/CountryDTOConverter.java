package fontys.sem3.school.business.impl;

import fontys.sem3.school.domain.Country;
import fontys.sem3.school.repository.entity.CountryEntity;

final class CountryDTOConverter {
    private CountryDTOConverter() {
    }

    public static Country convertToDTO(CountryEntity country) {
        return Country.builder()
                .id(country.getId())
                .code(country.getCode())
                .name(country.getName())
                .build();
    }
}
