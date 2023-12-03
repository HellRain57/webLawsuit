export interface PlaintiffDto {
    INN?:  string
    OGRN?: string
    address?: string
    KPP?: string
    paymentAccount?: string
    correspondentAccount?: string
    bank?:  string
    BIC?:  string
    OKTMO?:  string
    representative?:  string
}

export interface DefendantDto {
    FIO?: string
    birthdate?: Date
    passport?: string
    INN?: string
    OGRN?: string
    address?: string
    title?: string
}

export interface AreaDto {
    number?:string
    area?: number
    address?:string
}

export interface ContractDto {
    date?: Date
    period?: string
    paymentPoint?: string
    number?: string
    sum?: number
    penaltyPoint?: string
}

export interface PretensionDto {
    overduePeriod?: string
    debt?: string       
    peny?: number       
    penalty?: number       
    date?: Date       
    refuseDate?: Date         
    refuseCause?: string       
    unlawRefuseCase?: string       
    due?: number
}

export interface GovermentAgencyDto {
    registeringGovermentAgency?: string
    recordNumber?: string
    registryDate?: Date
}

export interface RulesDto {
    isRentPeriod?: boolean
    isPeny?: boolean
    shouldBeRegistred?: boolean
    isLegal?: boolean
    hasPretentionAnswer?: boolean
}

export interface LawsuitDto {
    plantiff?: PlaintiffDto
    defendant?: DefendantDto
    area?: AreaDto
    contract?: ContractDto
    pretension?: PretensionDto
    agency?: GovermentAgencyDto
    rules?: RulesDto
}

export interface Error {
    description?: string
    details?: string
}

export interface Response {
    data: any
    errors?: Error[]
}