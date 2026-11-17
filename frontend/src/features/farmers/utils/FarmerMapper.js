export function toFarmerFormValues(farmer) {
  return {
    name: farmer?.name ?? "",
    phone: farmer?.phone ?? "",
    village: farmer?.village ?? "",
    address: farmer?.address ?? "",
    active: farmer?.active ?? true,
  };
}

export function toFarmerRequest(values) {
  return {
    name: values.name.trim(),
    phone: values.phone?.trim() || null,
    village: values.village?.trim() || null,
    address: values.address?.trim() || null,
    active: values.active,
  };
}
