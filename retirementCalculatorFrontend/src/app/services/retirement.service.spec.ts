import { TestBed } from '@angular/core/testing';

import { RetirementService } from './retirement.service';

describe('RetirementService', () => {
  let service: RetirementService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RetirementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
