export interface Retirement {
  currentAge: number;
  retirementAge: number;
  interestRate: number;
  lifestyleType: 'simple' | 'fancy';
}

export interface RetirementResult {
  currentAge: number;
  retirementAge: number;
  interestRate: number;
  lifestyleType: string;
  monthlyDeposit: number;
  futureValue: number;
}
