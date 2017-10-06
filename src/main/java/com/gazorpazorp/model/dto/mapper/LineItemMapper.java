package com.gazorpazorp.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.gazorpazorp.model.LineItem;
import com.gazorpazorp.model.dto.OrderLineItem;

@Mapper(componentModel = "spring")
public interface LineItemMapper {
	LineItemMapper INSTANCE = Mappers.getMapper(LineItemMapper.class);
	
	@Mapping(target="productId")
	@Mapping(target="productName", source="item.product.name")
	@Mapping(target="qty")
	@Mapping(target="price", source="item.product.price")
	@Mapping(target="imageThumbUrl", source="item.prodcut.imageThumbUrl")
	OrderLineItem lineItemToOrderLineItem(LineItem item);
}
